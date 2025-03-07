# RISC-V Spec Core

This project is a framework for formal verification/testing the consistency
between a Chisel-designed RISC-V processor and the instruction set
specification.
Including a configurable `RiscvCore` as a reference model to represent the
semantics of the RISC-V ISA document, as well as several `Helper` and `Checker`
to connect the user's processor design with the reference model and set
verification conditions.

Reference model support RV32/64IMC, Zicsr, MSU privilege level,
virtual-momory system with Sv39.

Read more detailed Chinese README: [中文说明](README.zh-CN.md).

## Table of Contents <!-- omit in toc -->

- [Usage](#usage)
  - [Import dependency](#import-dependency)
  - [Add Checker and Set Basic Instruction Information](#add-checker-and-set-basic-instruction-information)
  - [General Register](#general-register)
  - [CSR and Exception](#csr-and-exception)
  - [TLB](#tlb)
  - [Memory Access](#memory-access)
- [Verification Example](#verification-example)
- [Publications](#publications)

## Usage

### Import dependency

To use riscv-spec-core as a managed dependency, add this in your `build.sbt`:

```scala
libraryDependencies += "cn.ac.ios.tis" %% "riscvspeccore" % "1.3-SNAPSHOT"
```

Then add verification code in your DUT as the following description.
Or see the [example](#verification-example).

### Add Checker and Set Basic Instruction Information

Instantiation a `checker`, and set the supportted instruction set of reference
module at the instruction commit level.

```scala
val rvConfig = RVConfig(64, "MCS", "A")
val checker = Module(new CheckerWithResult(checkMem = true)(rvConfig))

checker.io.instCommit.valid := XXX
checker.io.instCommit.inst  := XXX
checker.io.instCommit.pc    := XXX

ConnectCheckerResult.setChecker(checker)(XLEN, rvConfig)
```

### General Register

```scala
val resultRegWire = Wire(Vec(32, UInt(XLEN.W)))
resultRegWire := rf
resultRegWire(0) := 0.U
ConnectCheckerResult.setRegSource(resultRegWire)
```

### CSR and Exception

```scala
// CSR
val resultCSRWire = rvspeccore.checker.ConnectCheckerResult.makeCSRSource()(64, rvConfig)
resultCSRWire.misa      := RegNext(misa)
resultCSRWire.mvendorid := XXX
resultCSRWire.marchid   := XXX
// ······
// exception
val resultEventWire = rvspeccore.checker.ConnectCheckerResult.makeEventSource()(64, rvConfig)
resultEventWire.valid := XXX
resultEventWire.intrNO := XXX
resultEventWire.cause := XXX
resultEventWire.exceptionPC := XXX
resultEventWire.exceptionInst := XXX
// ······
```

### TLB

Get signals in TLB of DUT.

```scala
val resultTLBWire = rvspeccore.checker.ConnectCheckerResult.makeTLBSource(if(tlbname == "itlb") false else true)(64)
// memory access in TLB
resultTLBWire.read.valid := true.B
resultTLBWire.read.addr  := io.mem.req.bits.addr
resultTLBWire.read.data  := io.mem.resp.bits.rdata
resultTLBWire.read.level := (level-1.U)
// ······
```

### Memory Access

Get the signal when DUT access memory.

```scala
val mem = rvspeccore.checker.ConnectCheckerResult.makeMemSource()(64)
when(backend.io.dmem.resp.fire) {
    // load or store complete
    when(isRead) {
        isRead       := false.B
        mem.read.valid := true.B
        mem.read.addr  := SignExt(addr, 64)
        mem.read.data  := backend.io.dmem.resp.bits.rdata
        mem.read.memWidth := width
    }.elsewhen(isWrite) {
        isWrite       := false.B
        mem.write.valid := true.B
        mem.write.addr  := SignExt(addr, 64)
        mem.write.data  := wdata
        mem.write.memWidth := width
        // pass addr wdata wmask
    }.otherwise {
        // assert(false.B)
        // may receive some acceptable error resp, but microstructure can handle
    }
}
```

## Verification Example

[nutshell-fv](https://github.com/iscas-tis/nutshell-fv)  
In this example of processor design, we modified the code to get a verifiable
system with reference model.
And then perform formal verification using BMC through ChiselTest.

## Publications

If our work has been helpful to you, please cite:

**SETTA 2024: Formal Verification of RISC-V Processor Chisel Designs** [Link](https://link.springer.com/chapter/10.1007/978-981-96-0602-3_8) | [BibTex](https://citation-needed.springer.com/v2/references/10.1007/978-981-96-0602-3_8?format=bibtex&flavour=citation)


sbt compile
sbt clean compile
sbt "runMain rvspeccore.interface_app"
发现一个史上最傻逼的事情：在错误的目录运行代码导致项目跑不通
这里推荐有sbt的地方的所在文件夹作为项目的根目录，否则会寄了，哎真是逆天

有时候不得不觉得这写的代码是shit
居然不设计顶层模块，直接偷懒

必须在metals中打开导入配置，尽量不要在多个项目中使用metals跳转，否则会崩溃；


考虑的几个问题：
1.尽量少对原始形式化验证代码做修改
2.如果有的CPU缺少这方面的形式化验证，或者不支持部分指令，是报错还是warning? 所以统一的写法要改变
3.使用blackbox把veirlog进行封装，这是最简单的办法

大致思路：
1.用参考模型接参考模型测试下
2.考虑参数传递的问题，如何在参考模型中体现
3.如何接参考模型的测试信号hex文件等
4.找一个参考模型然后运行下看能不能接上去


在ssh服务器上配置公钥：
```shell
ssh-keygen -t rsa -b 4096 -C "1824161941@qq.com"
eval "$(ssh-agent -s)"
ls ~/.ssh
ssh-add -l
cat ~/.ssh/id_rsa.pub
copy
ssh -T git@github.com
git remote -v
git remote set-url origin git@github.com:l1x2h3/rvspeccore_verilog.git
git remote -v
git add .
git commit -m xxx
git push
```

### 这里继续做一个总结

1.关于DUT的选用问题:ysyx的项目貌似大部分都不满足，需要提供差分测试接口，但是提供了又因为CPU的类型不满足，类型满足了又因为是其他语言编写的，没有提供完整的运行文档

2.对CVA6的测试：
测试工作其实一直比较麻烦，尤其是core的复杂度，还没有构建差分测试接口
办法是：自己找办法接线，可以先考虑从基础框架ysyx做起，然后提高core的复杂度

3.信号的参数化问题：表面上看似接口写完了，但是每改一个参数，对应接口都会变
有两种处理思路：一个是要求DUT提供一个参数信息进行编译
另一个是直接实现完备的参数信号差，对于未知或用不上的信号用 ` _ , bx, .* `进行传递