package rvspeccore.checker

import chisel3._
import chisel3.util._

import rvspeccore.core._
import rvspeccore.core.spec.instset.csr.EventSig
import rvspeccore.core.tool.TLBMemInfo
import rvspeccore.core.tool.TLBSig

abstract class Checker()(implicit config: RVConfig) extends Module {
  implicit val XLEN: Int = config.XLEN
}

class InstCommit()(implicit XLEN: Int) extends Bundle {
  val valid = Bool()
  val inst  = UInt(32.W)
  val pc    = UInt(XLEN.W)
}
object InstCommit {
  def apply()(implicit XLEN: Int) = new InstCommit
}
class StoreOrLoadInfo(implicit XLEN: Int) extends Bundle {
  val addr     = UInt(XLEN.W)
  val data     = UInt(XLEN.W)
  val memWidth = UInt(log2Ceil(XLEN + 1).W)
}
class StoreOrLoadInfoTLB(implicit XLEN: Int) extends Bundle {
  val addr  = UInt(XLEN.W)
  val data  = UInt(XLEN.W)
  val level = UInt(log2Ceil(XLEN + 1).W)
}
class QueueModule(implicit XLEN: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Flipped(Decoupled(new StoreOrLoadInfo()))
    val out = Decoupled(new StoreOrLoadInfo())
  })

  val queue = Queue(io.in, 2)
  io.out <> queue
}
class QueueModuleTLB(implicit XLEN: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Flipped(Decoupled(new StoreOrLoadInfoTLB()))
    val out = Decoupled(new StoreOrLoadInfoTLB())
  })

  val queue = Queue(io.in, 2)
  io.out <> queue
}

/** RVFI_IO: RISC-V Formal Interface Bundle
  * Defines the output signals required for RISC-V Formal Verification Interface (RVFI).
  */
class RVFI_IO extends Bundle {
  // Instruction-related signals
  val valid     = Output(Bool())          // Indicates if the instruction is valid and committed
  val insn      = Output(UInt(32.W))      // The 32-bit instruction being executed
  val pc_rdata  = Output(UInt(64.W))      // Current Program Counter (PC) value
  val pc_wdata  = Output(UInt(64.W))      // Next PC value after instruction execution

  // Register-related signals
  val rs1_addr  = Output(UInt(5.W))       // Source register 1 address (5-bit index)
  val rs2_addr  = Output(UInt(5.W))       // Source register 2 address (5-bit index)
  val rs1_rdata = Output(UInt(64.W))      // Source register 1 read data (64-bit value)
  val rs2_rdata = Output(UInt(64.W))      // Source register 2 read data (64-bit value)
  val rd_addr   = Output(UInt(5.W))       // Destination register address (5-bit index)
  val rd_wdata  = Output(UInt(64.W))      // Destination register write data (64-bit value)
  val regs      = Vec(32, Output(UInt(64.W))) // Entire register file state (32 x 64-bit)

  // Memory-related signals
  val mem_addr  = Output(UInt(32.W))      // Memory access address (32-bit)
  val mem_rdata = Output(UInt(64.W))      // Memory read data (64-bit)
  val mem_wdata = Output(UInt(64.W))      // Memory write data (64-bit)
}

/** Checker with result port.
  *
  * Check pc of commited instruction and next value of all register. Although
  * `pc` in the result port, but it won't be checked.
  */

 import rvspeccore.interface.CoreInterface


class CheckerWithResult(val checkMem: Boolean = true, enableReg: Boolean = false)(implicit config: RVConfig)
    extends Checker {
    val io = IO(new Bundle {
      //val coreInterface = Flipped(new CoreInterface) // 适配 CoreInterface
      // 处理核心逻辑
      val instCommit = Input(InstCommit())
      val result     = Input(State())
      val event      = Input(new EventSig())
      val mem        = if (checkMem) Some(Input(new MemIO)) else None
      val dtlbmem    = if (checkMem && config.functions.tlb) Some(Input(new TLBSig)) else None
      val itlbmem    = if (checkMem && config.functions.tlb) Some(Input(new TLBSig)) else None

      //val rvfi       = Output(new RVFI_IO())
    })

  // TODO: io.result has .internal states now, consider use it or not

  /** Delay input data by a register if `delay` is true.
    *
    * This function helps to get signal values from the counterexample that only
    * contains values of registers from model checking.
    */
  def regDelay[T <: Data](data: T): T = {
    if (enableReg) RegNext(data, 0.U.asTypeOf(data.cloneType)) else data
  }


  // link to spec core
  val specCore = Module(new RiscvCore)
  specCore.io.valid := io.instCommit.valid
  specCore.io.inst  := io.instCommit.inst

  // initial another io.mem.get.Anotherread
  if (config.functions.tlb) {
    for (i <- 0 until 6) {
      specCore.io.tlb.get.Anotherread(i).data := DontCare
    }
  }

  // assertions

  if (checkMem) {
    if (!config.functions.tlb) {
      assert(regDelay(io.mem.get.read.valid) === regDelay(specCore.io.mem.read.valid))
      when(regDelay(io.mem.get.read.valid || specCore.io.mem.read.valid)) {
        assert(regDelay(io.mem.get.read.addr) === regDelay(specCore.io.mem.read.addr))
        assert(regDelay(io.mem.get.read.memWidth) === regDelay(specCore.io.mem.read.memWidth))
      }
      assert(regDelay(io.mem.get.write.valid) === regDelay(specCore.io.mem.write.valid))
      when(regDelay(io.mem.get.write.valid || specCore.io.mem.write.valid)) {
        assert(regDelay(io.mem.get.write.addr) === regDelay(specCore.io.mem.write.addr))
        assert(regDelay(io.mem.get.write.data) === regDelay(specCore.io.mem.write.data))
        assert(regDelay(io.mem.get.write.memWidth) === regDelay(specCore.io.mem.write.memWidth))
      }
      specCore.io.mem.read.data := io.mem.get.read.data
    } else {
      // printf("[SpecCore] Valid:%x PC: %x Inst: %x\n", specCore.io.valid, specCore.io.now.pc, specCore.io.inst)
      // specCore.io.mem.read.data := { if (checkMem) io.mem.get.read.data else DontCare }
      val TLBLoadQueue = Seq.fill(3)(Module(new QueueModuleTLB()))
      // initial the queue
      for (i <- 0 until 3) {
        TLBLoadQueue(i).io.out.ready := false.B
        TLBLoadQueue(i).io.in.valid  := false.B
        TLBLoadQueue(i).io.in.bits   := 0.U.asTypeOf(new StoreOrLoadInfoTLB)
      }
      when(io.dtlbmem.get.read.valid) {

        for (i <- 0 until 3) {
          when(io.dtlbmem.get.read.level === i.U) {
            TLBLoadQueue(i).io.in.valid      := true.B
            TLBLoadQueue(i).io.in.bits.addr  := io.dtlbmem.get.read.addr
            TLBLoadQueue(i).io.in.bits.data  := io.dtlbmem.get.read.data
            TLBLoadQueue(i).io.in.bits.level := io.dtlbmem.get.read.level
          }
        }
      }
      for (i <- 0 until 3) {
        when(specCore.io.tlb.get.Anotherread(i).valid) {
          TLBLoadQueue(2 - i).io.out.ready := true.B
          // printf("[SpecCore] Load out Queue Valid: %x %x %x %x\n", LoadQueue.io.out.valid, LoadQueue.io.out.bits.addr, LoadQueue.io.out.bits.data, LoadQueue.io.out.bits.memWidth)
          specCore.io.tlb.get.Anotherread(i).data := {
            if (checkMem) TLBLoadQueue(2 - i).io.out.bits.data else DontCare
          }
        }
        when(regDelay(specCore.io.tlb.get.Anotherread(i).valid)) {
          assert(regDelay(TLBLoadQueue(2 - i).io.out.bits.addr) === regDelay(specCore.io.tlb.get.Anotherread(i).addr))
        }
      }
      val LoadQueue  = Module(new QueueModule)
      val StoreQueue = Module(new QueueModule)
      // LOAD
      when(io.mem.get.read.valid) {
        LoadQueue.io.in.valid         := true.B
        LoadQueue.io.in.bits.addr     := io.mem.get.read.addr
        LoadQueue.io.in.bits.data     := io.mem.get.read.data
        LoadQueue.io.in.bits.memWidth := io.mem.get.read.memWidth
        // printf("[SpecCore] Load into Queue Valid: %x %x %x %x\n", LoadQueue.io.in.valid, load_push.addr, load_push.data, load_push.memWidth)
      }.otherwise {
        LoadQueue.io.in.valid := false.B
        LoadQueue.io.in.bits  := 0.U.asTypeOf(new StoreOrLoadInfo)
      }
      when(regDelay(specCore.io.mem.read.valid)) {
        LoadQueue.io.out.ready := true.B
        // printf("[SpecCore] Load out Queue Valid: %x %x %x %x\n", LoadQueue.io.out.valid, LoadQueue.io.out.bits.addr, LoadQueue.io.out.bits.data, LoadQueue.io.out.bits.memWidth)
        specCore.io.mem.read.data := LoadQueue.io.out.bits.data
        assert(regDelay(LoadQueue.io.out.bits.addr) === regDelay(specCore.io.mem.read.addr))
        assert(regDelay(LoadQueue.io.out.bits.memWidth) === regDelay(specCore.io.mem.read.memWidth))
      }.otherwise {
        LoadQueue.io.out.ready    := false.B
        specCore.io.mem.read.data := 0.U
      }

      // Store
      when(io.mem.get.write.valid) {
        StoreQueue.io.in.valid         := true.B
        StoreQueue.io.in.bits.addr     := io.mem.get.write.addr
        StoreQueue.io.in.bits.data     := io.mem.get.write.data
        StoreQueue.io.in.bits.memWidth := io.mem.get.write.memWidth
        // printf("[SpecCore] Store into Queue Valid: %x %x %x %x\n", StoreQueue.io.in.valid, store_push.addr, store_push.data, store_push.memWidth)
      }.otherwise {
        StoreQueue.io.in.valid := false.B
        StoreQueue.io.in.bits  := 0.U.asTypeOf(new StoreOrLoadInfo)
      }
      when(regDelay(specCore.io.mem.write.valid)) {
        StoreQueue.io.out.ready := true.B
        // printf("[SpecCore] Store out Queue  Valid: %x %x %x %x\n", StoreQueue.io.out.valid, StoreQueue.io.out.bits.addr, StoreQueue.io.out.bits.data, StoreQueue.io.out.bits.memWidth)
        assert(regDelay(StoreQueue.io.out.bits.addr) === regDelay(specCore.io.mem.write.addr))
        assert(regDelay(StoreQueue.io.out.bits.data) === regDelay(specCore.io.mem.write.data))
        assert(regDelay(StoreQueue.io.out.bits.memWidth) === regDelay(specCore.io.mem.write.memWidth))
      }.otherwise {
        StoreQueue.io.out.ready := false.B
      }
    }
  } else {
    specCore.io.mem.read.data := DontCare
  }

  when(regDelay(io.instCommit.valid)) {
    // now pc:
    assert(regDelay(io.instCommit.pc) === regDelay(specCore.io.now.pc))
    // next pc: hard to get next pc in a pipeline, check it at next instruction
    // next csr:
    io.result.csr.table.zip(specCore.io.next.csr.table).map {
      case (result, next) => {
        assert(regDelay(result.signal) === regDelay(next.signal))
      }
    }
    // next reg
    for (i <- 0 until 32) {
      assert(regDelay(io.result.reg(i.U)) === regDelay(specCore.io.next.reg(i.U)))
    }
  }

  when(regDelay(io.event.valid) || regDelay(specCore.io.event.valid)) {
    assert(
      regDelay(io.event.valid) === regDelay(specCore.io.event.valid)
    ) // Make sure DUT and specCore currently occur the same exception
    assert(regDelay(io.event.intrNO) === regDelay(specCore.io.event.intrNO))
    assert(regDelay(io.event.cause) === regDelay(specCore.io.event.cause))
    assert(regDelay(io.event.exceptionPC) === regDelay(specCore.io.event.exceptionPC))
    assert(regDelay(io.event.exceptionInst) === regDelay(specCore.io.event.exceptionInst))
  }

  // io.rvfi.valid      := regDelay(io.instCommit.valid)  // 指令提交有效性
  // io.rvfi.insn       := regDelay(io.instCommit.inst)   // 当前指令
  // io.rvfi.pc_rdata   := regDelay(io.instCommit.pc)     // 当前 PC
  // io.rvfi.pc_wdata   := regDelay(specCore.io.next.pc)  // 下一条指令的 PC
  // io.rvfi.rs1_addr   := io.instCommit.inst(19, 15)     // 源寄存器 1 地址（从指令中提取）
  // io.rvfi.rs2_addr   := io.instCommit.inst(24, 20)     // 源寄存器 2 地址（从指令中提取）
  // io.rvfi.rs1_rdata  := regDelay(specCore.io.now.reg(io.rvfi.rs1_addr))  // 源寄存器 1 数据
  // io.rvfi.rs2_rdata  := regDelay(specCore.io.now.reg(io.rvfi.rs2_addr))  // 源寄存器 2 数据
  // io.rvfi.rd_addr    := io.instCommit.inst(11, 7)      // 目标寄存器地址（从指令中提取）
  // io.rvfi.rd_wdata   := regDelay(specCore.io.next.reg(io.rvfi.rd_addr))  // 目标寄存器写入数据
  // io.rvfi.mem_addr   := regDelay(if (checkMem) io.mem.get.read.addr else 0.U(32.W))  // 内存访问地址
  // io.rvfi.mem_rdata  := regDelay(if (checkMem) io.mem.get.read.data else 0.U(64.W))  // 内存读取数据
  // io.rvfi.mem_wdata  := regDelay(if (checkMem) io.mem.get.write.data else 0.U(64.W)) // 内存写入数据
  // io.rvfi.regs       := VecInit((0 until 32).map(i => regDelay(specCore.io.next.reg(i.U)))) // 寄存器堆状态
}


class WriteBack()(implicit XLEN: Int) extends Bundle {
  val valid = Bool()
  val dest  = UInt(5.W)
  val data  = UInt(XLEN.W)
}
object WriteBack {
  def apply()(implicit XLEN: Int) = new WriteBack
}

/** Checker with write back port.
  *
  * Check pc of commited instruction and the register been write back.
  */
class CheckerWithWB(checkMem: Boolean = true)(implicit config: RVConfig) extends Checker {
  val io = IO(new Bundle {
    val instCommit = Input(InstCommit())
    val wb         = Input(WriteBack())
    val mem        = if (checkMem) Some(Input(new MemIO)) else None
  })

  // link to spec core
  val specCore = Module(new RiscvCore)
  specCore.io.valid := io.instCommit.valid
  specCore.io.inst  := io.instCommit.inst

  specCore.io.mem.read.data := { if (checkMem) io.mem.get.read.data else DontCare }

  // initial another io.mem.get.Anotherread
  if (config.functions.tlb) {
    for (i <- 0 until 6) {
      specCore.io.tlb.get.Anotherread(i).data := DontCare
    }
  }
  val specCoreWBValid = WireInit(false.B)
  val specCoreWBDest  = WireInit(0.U(5.W))
  for (i <- 0 until 32) {
    when(specCore.io.now.reg(i) =/= specCore.io.next.reg(i)) {
      specCoreWBValid := true.B
      specCoreWBDest  := i.U
    }
  }

  // assert in current clock
  when(io.instCommit.valid) {
    // now pc
    assert(io.instCommit.pc === specCore.io.now.pc)
    // next reg
    when(specCoreWBValid) {
      // prevent DUT not rise a write back
      assert(io.wb.dest === specCoreWBDest)
      assert(io.wb.data === specCore.io.next.reg(io.wb.dest))
    }.otherwise {
      // DUT may try to write back to x0, but it should not take effect
      // if DUT dose write in x0, it will be check out at next instruction
      when(io.wb.valid && io.wb.dest =/= 0.U) {
        assert(io.wb.data === specCore.io.next.reg(io.wb.dest))
      }
    }
    // next pc: no next pc signal in this case
    if (checkMem) {
      assert(io.mem.get.read.valid === specCore.io.mem.read.valid)
      assert(io.mem.get.read.addr === specCore.io.mem.read.addr)
      assert(io.mem.get.read.memWidth === specCore.io.mem.read.memWidth)

      assert(io.mem.get.write.valid === specCore.io.mem.write.valid)
      assert(io.mem.get.write.addr === specCore.io.mem.write.addr)
      assert(io.mem.get.write.memWidth === specCore.io.mem.write.memWidth)
      assert(io.mem.get.write.data === specCore.io.mem.write.data)
    }
  }
}



import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util._

class CheckerWrapper(val checkMem: Boolean = true, enableReg: Boolean = false)(implicit config: RVConfig) extends Module {
  val io = IO(new Bundle {
    implicit val XLEN: Int = config.XLEN
    val instCommit = Input(InstCommit())
    val result     = Input(State())
    val event      = Input(new EventSig())
    val mem        = if (checkMem) Some(Input(new MemIO)) else None
    val dtlbmem    = if (checkMem && config.functions.tlb) Some(Input(new TLBSig)) else None
    val itlbmem    = if (checkMem && config.functions.tlb) Some(Input(new TLBSig)) else None
    // 添加 RVFI_IO 输出接口
    //val rvfi       = Output(new RVFI_IO())
  })

  // 实例化 CheckerWithResult
  val checker = Module(new CheckerWithResult(checkMem, enableReg))
  
  // 连接输入信号
  checker.io.instCommit := io.instCommit
  checker.io.result     := io.result
  checker.io.event      := io.event
  
  if (checkMem) {
    checker.io.mem.get := io.mem.get
    if (config.functions.tlb) {
      checker.io.dtlbmem.get := io.dtlbmem.get
      checker.io.itlbmem.get := io.itlbmem.get
    }
  }

  // 假设 CheckerWithResult 提供 RVFI 信号，将其连接到 io.rvfi
  // 如果 CheckerWithResult 没有直接提供 RVFI_IO，你需要手动映射信号
  //io.rvfi := checker.io.rvfi  // 需要确保 checker 模块有 rvfi 输出
}


object Main extends App {
  // 使用 RVConfig 的 apply 方法创建配置
  implicit val config: RVConfig = RVConfig(
    XLEN = 64,
    extensions = "MCZicsrU",
    fakeExtensions = "A",
    initValue = Map("pc" -> "h00008000"),
    functions = Seq(
      "Privileged",
      "TLB"
    )
  )

  // 生成 Verilog 代码
  (new ChiselStage).emitSystemVerilog(
    new CheckerWrapper(checkMem = true, enableReg = false),
    Array("--target-dir", "generated_full_para", "--no-dce")
  )
}