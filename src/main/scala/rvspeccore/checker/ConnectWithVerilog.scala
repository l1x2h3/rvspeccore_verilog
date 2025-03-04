// package rvspeccore.checker

// import chisel3._
// import chisel3.util._
// import chisel3.util.experimental.BoringUtils
// import chisel3.experimental.ExtModule
// import rvspeccore.checker
// import rvspeccore.core.RVConfig

// import rvspeccore.core.RVConfig
// import rvspeccore.core.spec.instset.csr.CSR
// import rvspeccore.core.spec.instset.csr.EventSig
// import rvspeccore.core.tool.TLBSig

// class VerilogRiscvCore extends ExtModule {
//   // 时钟和复位信号
//   val clock  = IO(Input(Clock()))
//   val reset  = IO(Input(Bool()))

//   // 输入信号
//   val io_inst     = IO(Input(UInt(32.W)))
//   val io_valid    = IO(Input(Bool()))

//   // 输出信号
//   val io_iFetchpc = IO(Output(UInt(64.W)))
//   val io_mem_read_valid    = IO(Output(Bool()))
//   val io_mem_read_addr     = IO(Output(UInt(64.W)))
//   val io_mem_read_memWidth = IO(Output(UInt(7.W)))
//   val io_mem_read_data     = IO(Input(UInt(64.W)))
//   val io_mem_write_valid   = IO(Output(Bool()))
//   val io_mem_write_addr    = IO(Output(UInt(64.W)))
//   val io_mem_write_memWidth= IO(Output(UInt(7.W)))
//   val io_mem_write_data    = IO(Output(UInt(64.W)))

//   // 状态信号
//   val io_now_reg_0  = IO(Output(UInt(64.W)))
//   val io_now_reg_1  = IO(Output(UInt(64.W)))
//   val io_now_reg_2  = IO(Output(UInt(64.W)))
//   val io_now_reg_3  = IO(Output(UInt(64.W)))
//   val io_now_reg_4  = IO(Output(UInt(64.W)))
//   val io_now_reg_5  = IO(Output(UInt(64.W)))
//   val io_now_reg_6  = IO(Output(UInt(64.W)))
//   val io_now_reg_7  = IO(Output(UInt(64.W)))
//   val io_now_reg_8  = IO(Output(UInt(64.W)))
//   val io_now_reg_9  = IO(Output(UInt(64.W)))
//   val io_now_reg_10 = IO(Output(UInt(64.W)))
//   val io_now_reg_11 = IO(Output(UInt(64.W)))
//   val io_now_reg_12 = IO(Output(UInt(64.W)))
//   val io_now_reg_13 = IO(Output(UInt(64.W)))
//   val io_now_reg_14 = IO(Output(UInt(64.W)))
//   val io_now_reg_15 = IO(Output(UInt(64.W)))
//   val io_now_reg_16 = IO(Output(UInt(64.W)))
//   val io_now_reg_17 = IO(Output(UInt(64.W)))
//   val io_now_reg_18 = IO(Output(UInt(64.W)))
//   val io_now_reg_19 = IO(Output(UInt(64.W)))
//   val io_now_reg_20 = IO(Output(UInt(64.W)))
//   val io_now_reg_21 = IO(Output(UInt(64.W)))
//   val io_now_reg_22 = IO(Output(UInt(64.W)))
//   val io_now_reg_23 = IO(Output(UInt(64.W)))
//   val io_now_reg_24 = IO(Output(UInt(64.W)))
//   val io_now_reg_25 = IO(Output(UInt(64.W)))
//   val io_now_reg_26 = IO(Output(UInt(64.W)))
//   val io_now_reg_27 = IO(Output(UInt(64.W)))
//   val io_now_reg_28 = IO(Output(UInt(64.W)))
//   val io_now_reg_29 = IO(Output(UInt(64.W)))
//   val io_now_reg_30 = IO(Output(UInt(64.W)))
//   val io_now_reg_31 = IO(Output(UInt(64.W)))

//   // ... 其他寄存器信号
//   val io_now_pc     = IO(Output(UInt(64.W)))
//   val io_now_csr_misa = IO(Output(UInt(64.W)))

//   var io_now_csr_mvendorid = IO(Output(UInt(64.W)))
//   var io_now_csr_marchid  = IO(Output(UInt(64.W)))
//   var io_now_csr_mimpid  = IO(Output(UInt(64.W)))
//   var io_now_csr_mhartid  = IO(Output(UInt(64.W)))
//   var io_now_csr_mstatus  = IO(Output(UInt(64.W)))
//   var io_now_csr_mstatush = IO(Output(UInt(64.W)))
//   var io_now_csr_mscratch = IO(Output(UInt(64.W)))
//   var io_now_csr_mtvec = IO(Output(UInt(64.W)))
//   var io_now_csr_mcounteren = IO(Output(UInt(64.W)))
//   var io_now_csr_medeleg = IO(Output(UInt(64.W)))
//   var io_now_csr_mideleg = IO(Output(UInt(64.W)))
//   var io_now_csr_mip = IO(Output(UInt(64.W)))
//   var io_now_csr_mie = IO(Output(UInt(64.W)))
//   var io_now_csr_mepc = IO(Output(UInt(64.W)))
//   var io_now_csr_mcause = IO(Output(UInt(64.W)))
//   var io_now_csr_mtval = IO(Output(UInt(64.W)))
//   var io_now_csr_cycle = IO(Output(UInt(64.W)))
//   var io_now_csr_scounteren = IO(Output(UInt(64.W)))
//   var io_now_csr_scause = IO(Output(UInt(64.W)))
//   var io_now_csr_stvec = IO(Output(UInt(64.W)))
//   var io_now_csr_sepc = IO(Output(UInt(64.W)))
//   var io_now_csr_stval = IO(Output(UInt(64.W)))
//   var io_now_csr_sscratch = IO(Output(UInt(64.W)))
//   var io_now_csr_satp = IO(Output(UInt(64.W)))
//   var io_now_csr_pmpcfg0 = IO(Output(UInt(64.W)))
//   var io_now_csr_pmpcfg1 = IO(Output(UInt(64.W)))
//   var io_now_csr_pmpcfg2 = IO(Output(UInt(64.W)))
//   var io_now_csr_pmpcfg3 = IO(Output(UInt(64.W)))
//   var io_now_csr_pmpaddr0 = IO(Output(UInt(64.W)))
//   var io_now_csr_pmpaddr1 = IO(Output(UInt(64.W)))
//   var io_now_csr_pmpaddr2 = IO(Output(UInt(64.W)))
//   var io_now_csr_pmpaddr3 = IO(Output(UInt(64.W)))
//   var io_now_csr_MXLEN = IO(Output(UInt(64.W)))
//   var io_now_csr_IALIGN = IO(Output(UInt(64.W)))
//   var io_now_csr_ILEN = IO(Output(UInt(64.W)))
//   var io_now_internal_privilegeMode = IO(Output(UInt(64.W)))
//   var io_next_reg_0 = IO(Output(UInt(64.W)))
//   var io_next_reg_1 = IO(Output(UInt(64.W)))
//   var io_next_reg_2 = IO(Output(UInt(64.W)))
//   var io_next_reg_3 = IO(Output(UInt(64.W)))
//   var io_next_reg_4 = IO(Output(UInt(64.W)))
//   var io_next_reg_5 = IO(Output(UInt(64.W)))
//   var io_next_reg_6 = IO(Output(UInt(64.W)))
//   var io_next_reg_7 = IO(Output(UInt(64.W)))
//   var io_next_reg_8 = IO(Output(UInt(64.W)))
//   var io_next_reg_9 = IO(Output(UInt(64.W)))
//   var io_next_reg_10 = IO(Output(UInt(64.W)))
//   var io_next_reg_11 = IO(Output(UInt(64.W)))
//   var io_next_reg_12 = IO(Output(UInt(64.W)))
//   var io_next_reg_13 = IO(Output(UInt(64.W)))
//   var io_next_reg_14 = IO(Output(UInt(64.W)))
//   var io_next_reg_15 = IO(Output(UInt(64.W)))
//   var io_next_reg_16 = IO(Output(UInt(64.W)))
//   var io_next_reg_17 = IO(Output(UInt(64.W)))
//   var io_next_reg_18 = IO(Output(UInt(64.W)))
//   var io_next_reg_19 = IO(Output(UInt(64.W)))
//   var io_next_reg_20 = IO(Output(UInt(64.W)))
//   var io_next_reg_21 = IO(Output(UInt(64.W)))
//   var io_next_reg_22 = IO(Output(UInt(64.W)))
//   var io_next_reg_23 = IO(Output(UInt(64.W)))
//   var io_next_reg_24 = IO(Output(UInt(64.W)))
//   var io_next_reg_25 = IO(Output(UInt(64.W)))
//   var io_next_reg_26 = IO(Output(UInt(64.W)))
//   var io_next_reg_27 = IO(Output(UInt(64.W)))
//   var io_next_reg_28 = IO(Output(UInt(64.W)))
//   var io_next_reg_29 = IO(Output(UInt(64.W)))
//   var io_next_reg_30 = IO(Output(UInt(64.W)))
//   var io_next_reg_31 = IO(Output(UInt(64.W)))
//   var io_next_pc = IO(Output(UInt(64.W)))
//     // ... 其他 CSR 信号
//   var io_next_csr_misa = IO(Output(UInt(64.W)))
//   var io_next_csr_mvendorid = IO(Output(UInt(64.W)))
//   var io_next_csr_marchid = IO(Output(UInt(64.W)))
//   var io_next_csr_mimpid = IO(Output(UInt(64.W)))
//   var io_next_csr_mhartid = IO(Output(UInt(64.W)))
//   var io_next_csr_mstatus = IO(Output(UInt(64.W)))
//   var io_next_csr_mstatush = IO(Output(UInt(64.W)))
//   var io_next_csr_mscratch = IO(Output(UInt(64.W)))
//   var io_next_csr_mtvec = IO(Output(UInt(64.W)))
//   var io_next_csr_mcountere = IO(Output(UInt(64.W)))
//   var io_next_csr_medeleg = IO(Output(UInt(64.W)))
//   var io_next_csr_mideleg = IO(Output(UInt(64.W)))
//   var io_next_csr_mip = IO(Output(UInt(64.W)))
//   var io_next_csr_mie = IO(Output(UInt(64.W)))
//   var io_next_csr_mepc = IO(Output(UInt(64.W)))
//   var io_next_csr_mcause = IO(Output(UInt(64.W)))
//   var io_next_csr_mtval = IO(Output(UInt(64.W)))
//   var io_next_csr_cycle = IO(Output(UInt(64.W)))
//   var io_next_csr_scountere = IO(Output(UInt(64.W)))
//   var io_next_csr_scause = IO(Output(UInt(64.W)))
//   var io_next_csr_stvec = IO(Output(UInt(64.W)))
//   var io_next_csr_sepc = IO(Output(UInt(64.W)))
//   var io_next_csr_stval = IO(Output(UInt(64.W)))
//   var io_next_csr_sscratch = IO(Output(UInt(64.W)))
//   var io_next_csr_satp = IO(Output(UInt(64.W)))
//   var io_next_csr_pmpcfg0 = IO(Output(UInt(64.W)))
//   var io_next_csr_pmpcfg1 = IO(Output(UInt(64.W))) 
//   var io_next_csr_pmpcfg2 = IO(Output(UInt(64.W))) 
//   var io_next_csr_pmpcfg3 = IO(Output(UInt(64.W))) 
//   var io_next_csr_pmpaddr0 = IO(Output(UInt(64.W)))
//   var io_next_csr_pmpaddr1 = IO(Output(UInt(64.W)))
//   var io_next_csr_pmpaddr2 = IO(Output(UInt(64.W)))
//   var io_next_csr_pmpaddr3 = IO(Output(UInt(64.W)))


//   // 事件信号
//   val io_event_valid        = IO(Output(Bool()))
//   val io_event_intrNO       = IO(Output(UInt(64.W)))
//   val io_event_cause        = IO(Output(UInt(64.W)))
//   val io_event_exceptionPC  = IO(Output(UInt(64.W)))
//   val io_event_exceptionInst= IO(Output(UInt(64.W)))
// }

// abstract class ConnectHelperVerilog {}


// //寄存器的动态连接
// import scala.reflect.runtime.universe._


// object ConnectVerilogCore extends ConnectHelperVerilog {
//   val uniqueIdReg: String   = "ConnectVerilogCore_UniqueIdReg"
//   val uniqueIdMem: String   = "ConnectVerilogCore_UniqueIdMem"
//   val uniqueIdCSR: String   = "ConnectVerilogCore_UniqueIdCSR"
//   val uniqueIdEvent: String = "ConnectVerilogCore_UniqueIdEvent"

//   def connectRegisters(verilogCore: VerilogRiscvCore): Vec[UInt] = {
//     val regVec = Wire(Vec(32, UInt(64.W)))

//     // 使用反射动态访问端口
//     val mirror = runtimeMirror(verilogCore.getClass.getClassLoader)
//     val instanceMirror = mirror.reflect(verilogCore)

//       for (i <- 0 until 32) {
//           val fieldName = s"io_now_reg_$i"  // 动态生成字段名
//           val field = try {
//           verilogCore.getClass.getDeclaredField(fieldName)
//           } catch {
//           case _: NoSuchFieldException =>
//               throw new RuntimeException(s"Field $fieldName not found in VerilogRiscvCore")
//           }
//           field.setAccessible(true)
//           val fieldMirror = instanceMirror.reflectField(field)
//           regVec(i) := fieldMirror.get.asInstanceOf[UInt]  // 动态获取信号并连接
//       }
//       regVec
//     }

//   def setVerilogCoreSource(verilogCore: VerilogRiscvCore) = {
//     // 寄存器信号
//     val regVec = connectRegisters(verilogCore)
//     BoringUtils.addSource(regVec, uniqueIdReg)

//     // 内存信号
//     val mem = Wire(new MemSig)
//     mem.read.valid     := verilogCore.io_mem_read_valid
//     mem.read.addr      := verilogCore.io_mem_read_addr
//     mem.read.memWidth  := verilogCore.io_mem_read_memWidth
//     mem.read.data      := verilogCore.io_mem_read_data
//     mem.write.valid    := verilogCore.io_mem_write_valid
//     mem.write.addr     := verilogCore.io_mem_write_addr
//     mem.write.memWidth := verilogCore.io_mem_write_memWidth
//     mem.write.data     := verilogCore.io_mem_write_data
//     BoringUtils.addSource(mem, uniqueIdMem)

//     // CSR 信号
//     val csr = Wire(CSR())
//     csr.misa := verilogCore.io_now_csr_misa
//     // ... 其他 CSR 信号
//     BoringUtils.addSource(csr, uniqueIdCSR)

//     // 事件信号
//     val event = Wire(new EventSig)
//     event.valid         := verilogCore.io_event_valid
//     event.intrNO        := verilogCore.io_event_intrNO
//     event.cause         := verilogCore.io_event_cause
//     event.exceptionPC   := verilogCore.io_event_exceptionPC
//     event.exceptionInst := verilogCore.io_event_exceptionInst
//     BoringUtils.addSource(event, uniqueIdEvent)
//   }

//   def setChecker(checker: CheckerWithResult) = {
//     // 寄存器信号
//     val regVec = Wire(Vec(32, UInt(64.W)))
//     BoringUtils.addSink(regVec, uniqueIdReg)
//     checker.io.result.reg := regVec

//     // 内存信号
//     val mem = Wire(new MemSig)
//     BoringUtils.addSink(mem, uniqueIdMem)
//     checker.io.mem.get := mem

//     // CSR 信号
//     val csr = Wire(CSR())
//     BoringUtils.addSink(csr, uniqueIdCSR)
//     checker.io.result.csr := csr

//     // 事件信号
//     val event = Wire(new EventSig)
//     BoringUtils.addSink(event, uniqueIdEvent)
//     checker.io.event := event
//   }
// }


// class TopModule extends Module {
//   implicit val config: RVConfig = new RVConfig(XLEN = 64)

//   // 实例化 Verilog 核
//   val verilogCore = Module(new VerilogRiscvCore)

//   // 实例化 Checker
//   val checker = Module(new CheckerWithResult)

//   // 连接 Verilog 核和 Checker
//   ConnectVerilogCore.setVerilogCoreSource(verilogCore)
//   ConnectVerilogCore.setChecker(checker)

//   // 连接输入信号
//   verilogCore.io_inst  := DontCare  // 替换为实际输入
//   verilogCore.io_valid := DontCare  // 替换为实际输入

//   // 连接时钟和复位信号
//   verilogCore.clock := clock
//   verilogCore.reset := reset
// }

// import chisel3.stage.ChiselStage

// object Main extends App {
//   (new ChiselStage).emitVerilog(new TopModule, Array("--target-dir", "generated"))
// }