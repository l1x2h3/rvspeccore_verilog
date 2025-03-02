package rvspeccore.interface

import chisel3._
import chisel3.util._
import chisel3.experimental._
import rvspeccore.checker.CheckerWithResult
import rvspeccore.checker.CheckerWithWB
import rvspeccore.core.RVConfig
import chisel3.stage.ChiselStage
import scala.io.Source
import rvspeccore.checker._
import rvspeccore.core._


// Core 接口定义

class CoreInterface(implicit val config: RVConfig) extends Bundle {
  implicit val XLEN: Int = config.XLEN  // 隐式传递 XLEN
  implicit val extensions = Seq("I", "M", "C")
  val clk      = Input(Clock())
  val rst      = Input(Bool())
  val data_in  = Input(UInt(32.W))
  val data_out = Output(UInt(32.W))
  val valid    = Input(Bool())
  val ready    = Output(Bool())

  // 新增信号
  val instCommit = Input(new InstCommit())  // 指令提交信号
  val result     = Input(new State())       // 结果状态信号
  // val event      = Input(new EventSig())    // 事件信号
  // val mem        = if (config.functions.mem) Some(Input(new MemIO)) else None  // 内存接口
  // val dtlbmem    = if (config.functions.tlb) Some(Input(new TLBSig)) else None  // 数据 TLB 接口
  // val itlbmem    = if (config.functions.tlb) Some(Input(new TLBSig)) else None  // 指令 TLB 接口

  // 新增写回信号
  val wb         = Input(new WriteBack())   // 写回信号
}

// Verilog 核定义
class VerilogCore(implicit config: RVConfig) extends BlackBox with HasBlackBoxResource {
  val io = IO(new CoreInterface)
  val resourcePath = "../simple_core/simple_core.v"
  
  // 打印加载的资源路径，确保路径正确
  println(s"Attempting to load Verilog resource from: $resourcePath")
  
  addResource(resourcePath)  // 添加 Verilog 文件

  val file = new java.io.File(resourcePath)
  if (file.exists()) {
    println(s"Found Verilog file at: $resourcePath")  // 确保文件存在
  } else {
    println(s"ERROR: Verilog file not found at: $resourcePath")
  }
}

// 顶层模块
class TopModule(implicit config: RVConfig) extends Module {
  val io = IO(new Bundle {
    val data_in  = Input(UInt(32.W))
    val data_out = Output(UInt(32.W))
  })

  println("TopModule instantiated.")  // 确认是否进入到 TopModule 的构造函数

  val core    = Module(new VerilogCore)
  println("VerilogCore module created.")  // 确认 VerilogCore 是否创建成功

  // 创建 Checker
  val checker1 = Module(new CheckerWithResult()(config))
  val checker2 = Module(new CheckerWithWB()(config))

  println("CheckerWithResult module created.")  // 确认 Checker 是否创建成功

  // 连接 Core 和 Checker
  core.io.clk     := clock
  core.io.rst     := reset
  core.io.data_in := io.data_in
  io.data_out     := core.io.data_out

  println("Connections between Core and Checker set up.")  // 确认连接设置

  // 连接 CheckerWithResult 到 CoreInterface
  // checker1.io.coreInterface <> core.io

  // // 连接 CheckerWithWB 到 CoreInterface
  // checker2.io.coreInterface <> core.io
}

// 运行顶层模块并生成 Verilog 文件
object TopModule extends App {
  implicit val config: RVConfig = new RVConfig(
    "XLEN" -> 64,
    "extensions" -> Seq("I", "M", "C"),
    "functions" -> Seq("Privileged", "TLB", "Mem")
  )

  println("Starting Chisel stage to generate Verilog...")  // 打印开始生成 Verilog

  // 使用 ChiselStage 生成 Verilog 文件并指定输出路径
  val outputDir = "./generated_verilog"
  val chiselStage = new ChiselStage
  chiselStage.emitVerilog(new TopModule, Array("--target-dir", outputDir))
  
  println("Verilog generation complete.")  // 确认 Verilog 生成完成
  
  // 读取生成的 Verilog 文件并打印
  val verilogFile = s"$outputDir/TopModule.v"  // 假设生成的文件名是 TopModule.v
  val verilogContent = try {
    Source.fromFile(verilogFile).getLines.mkString("\n")
  } catch {
    case e: Exception => s"Failed to read Verilog file: ${e.getMessage}"
  }

  println("Generated Verilog content:\n")
  println(verilogContent)  // 打印生成的 Verilog 文件内容
}