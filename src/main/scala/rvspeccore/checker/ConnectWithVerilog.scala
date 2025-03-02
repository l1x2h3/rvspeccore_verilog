package rvspeccore.checker

import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import chisel3.experimental.ExtModule
import rvspeccore.checker
import rvspeccore.core.RVConfig

import rvspeccore.core.RVConfig
import rvspeccore.core.spec.instset.csr.CSR
import rvspeccore.core.spec.instset.csr.EventSig
import rvspeccore.core.tool.TLBSig

class VerilogRiscvCore extends ExtModule {
  // 时钟和复位信号
  val clock  = IO(Input(Clock()))
  val reset  = IO(Input(Bool()))

  // 输入信号
  val io_inst     = IO(Input(UInt(32.W)))
  val io_valid    = IO(Input(Bool()))

  // 输出信号
  val io_iFetchpc = IO(Output(UInt(64.W)))
  val io_mem_read_valid    = IO(Output(Bool()))
  val io_mem_read_addr     = IO(Output(UInt(64.W)))
  val io_mem_read_memWidth = IO(Output(UInt(7.W)))
  val io_mem_read_data     = IO(Input(UInt(64.W)))
  val io_mem_write_valid   = IO(Output(Bool()))
  val io_mem_write_addr    = IO(Output(UInt(64.W)))
  val io_mem_write_memWidth= IO(Output(UInt(7.W)))
  val io_mem_write_data    = IO(Output(UInt(64.W)))

  // 状态信号
  val io_now_reg_0  = IO(Output(UInt(64.W)))
  val io_now_reg_1  = IO(Output(UInt(64.W)))
  // ... 其他寄存器信号
  val io_now_pc     = IO(Output(UInt(64.W)))
  val io_now_csr_misa = IO(Output(UInt(64.W)))
  // ... 其他 CSR 信号

  // 事件信号
  val io_event_valid        = IO(Output(Bool()))
  val io_event_intrNO       = IO(Output(UInt(64.W)))
  val io_event_cause        = IO(Output(UInt(64.W)))
  val io_event_exceptionPC  = IO(Output(UInt(64.W)))
  val io_event_exceptionInst= IO(Output(UInt(64.W)))
}

abstract class ConnectHelperVerilog {}


//寄存器的动态连接
import scala.reflect.runtime.universe._




object ConnectVerilogCore extends ConnectHelperVerilog {
  val uniqueIdReg: String   = "ConnectVerilogCore_UniqueIdReg"
  val uniqueIdMem: String   = "ConnectVerilogCore_UniqueIdMem"
  val uniqueIdCSR: String   = "ConnectVerilogCore_UniqueIdCSR"
  val uniqueIdEvent: String = "ConnectVerilogCore_UniqueIdEvent"

  def connectRegisters(verilogCore: VerilogRiscvCore): Vec[UInt] = {
    val regVec = Wire(Vec(32, UInt(64.W)))

    // 使用反射动态访问端口
    val mirror = runtimeMirror(verilogCore.getClass.getClassLoader)
    val instanceMirror = mirror.reflect(verilogCore)

    for (i <- 0 until 32) {
        val fieldName = s"io_now_reg_$i"  // 动态生成字段名
        val field = try {
        verilogCore.getClass.getDeclaredField(fieldName)
        } catch {
        case _: NoSuchFieldException =>
            throw new RuntimeException(s"Field $fieldName not found in VerilogRiscvCore")
        }
        field.setAccessible(true)
        val fieldMirror = instanceMirror.reflectField(field)
        regVec(i) := fieldMirror.get.asInstanceOf[UInt]  // 动态获取信号并连接
    }
    regVec
    }

  def setVerilogCoreSource(verilogCore: VerilogRiscvCore) = {
    // 寄存器信号
    val regVec = connectRegisters(verilogCore)
    BoringUtils.addSource(regVec, uniqueIdReg)

    // 内存信号
    val mem = Wire(new MemSig)
    mem.read.valid     := verilogCore.io_mem_read_valid
    mem.read.addr      := verilogCore.io_mem_read_addr
    mem.read.memWidth  := verilogCore.io_mem_read_memWidth
    mem.read.data      := verilogCore.io_mem_read_data
    mem.write.valid    := verilogCore.io_mem_write_valid
    mem.write.addr     := verilogCore.io_mem_write_addr
    mem.write.memWidth := verilogCore.io_mem_write_memWidth
    mem.write.data     := verilogCore.io_mem_write_data
    BoringUtils.addSource(mem, uniqueIdMem)

    // CSR 信号
    val csr = Wire(CSR())
    csr.misa := verilogCore.io_now_csr_misa
    // ... 其他 CSR 信号
    BoringUtils.addSource(csr, uniqueIdCSR)

    // 事件信号
    val event = Wire(new EventSig)
    event.valid         := verilogCore.io_event_valid
    event.intrNO        := verilogCore.io_event_intrNO
    event.cause         := verilogCore.io_event_cause
    event.exceptionPC   := verilogCore.io_event_exceptionPC
    event.exceptionInst := verilogCore.io_event_exceptionInst
    BoringUtils.addSource(event, uniqueIdEvent)
  }

  def setChecker(checker: CheckerWithResult) = {
    // 寄存器信号
    val regVec = Wire(Vec(32, UInt(64.W)))
    BoringUtils.addSink(regVec, uniqueIdReg)
    checker.io.result.reg := regVec

    // 内存信号
    val mem = Wire(new MemSig)
    BoringUtils.addSink(mem, uniqueIdMem)
    checker.io.mem.get := mem

    // CSR 信号
    val csr = Wire(CSR())
    BoringUtils.addSink(csr, uniqueIdCSR)
    checker.io.result.csr := csr

    // 事件信号
    val event = Wire(new EventSig)
    BoringUtils.addSink(event, uniqueIdEvent)
    checker.io.event := event
  }
}


class TopModule extends Module {
  implicit val config: RVConfig = new RVConfig(XLEN = 64)

  // 实例化 Verilog 核
  val verilogCore = Module(new VerilogRiscvCore)

  // 实例化 Checker
  val checker = Module(new CheckerWithResult)

  // 连接 Verilog 核和 Checker
  ConnectVerilogCore.setVerilogCoreSource(verilogCore)
  ConnectVerilogCore.setChecker(checker)

  // 连接输入信号
  verilogCore.io_inst  := DontCare  // 替换为实际输入
  verilogCore.io_valid := DontCare  // 替换为实际输入

  // 连接时钟和复位信号
  verilogCore.clock := clock
  verilogCore.reset := reset
}

import chisel3.stage.ChiselStage

object Main extends App {
  (new ChiselStage).emitVerilog(new TopModule, Array("--target-dir", "generated"))
}