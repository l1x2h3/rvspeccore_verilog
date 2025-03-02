// package rvspeccore

// import chisel3._
// import chisel3.stage.ChiselStage
// import rvspeccore.core.RVConfig
// import rvspeccore.checker.CheckerWithResult
// import rvspeccore.interface.core_interface

// object interface_app extends App {
//   implicit val config: RVConfig = RVConfig(
//     XLEN = 64,
//     extensions = Seq("I", "M", "C"),
//     fakeExtensions = Seq("A", "B"),
//     initValue = Map("pc" -> "h8000_0000"),
//     functions = Seq("Privileged"),
//     formal = Seq.empty,
//     isChisel = true, // 是否使用 Chisel Core
//     vcdFile = "core.vcd" // Verilog Core 的 VCD 文件
//   )

//   val checker = new CheckerWithResult(checkMem = true, enableReg = false)

//   if (config.isChisel) {
//     (new ChiselStage).emitVerilog(checker, Array("--target-dir", "generated"))
//   } else {
//     // 运行 Verilog 形式化验证
//     runVerilogFormal(checker, config.vcdFile)
//   }
// }