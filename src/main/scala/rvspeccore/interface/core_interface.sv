module VerilogCoreInterface (
  input  wire         clock,
  input  wire         reset,
  // 指令提交信号
  output wire         instCommit_valid,
  output wire [31:0]  instCommit_inst,
  output wire [63:0]  instCommit_pc,
  // 状态信号
  output wire [63:0]  result_reg [0:31],
  output wire [63:0]  result_pc,
  output wire [63:0]  result_csr [0:4095],
  // 内存访问信号
  output wire         mem_read_valid,
  output wire [63:0]  mem_read_addr,
  input  wire [63:0]  mem_read_data,
  output wire [2:0]   mem_read_memWidth,
  output wire         mem_write_valid,
  output wire [63:0]  mem_write_addr,
  output wire [63:0]  mem_write_data,
  output wire [2:0]   mem_write_memWidth,
  // 事件信号
  output wire         event_valid,
  output wire [63:0]  event_intrNO,
  output wire [63:0]  event_cause,
  output wire [63:0]  event_exceptionPC,
  output wire [31:0]  event_exceptionInst
);
  // 将 Verilog Core 的信号连接到接口
  assign instCommit_valid = core.instCommit_valid;
  assign instCommit_inst  = core.instCommit_inst;
  assign instCommit_pc    = core.instCommit_pc;

  generate
    for (genvar i = 0; i < 32; i++) begin
      assign result_reg[i] = core.reg_file[i];
    end
  endgenerate
  assign result_pc = core.pc;
  generate
    for (genvar i = 0; i < 4096; i++) begin
      assign result_csr[i] = core.csr_file[i];
    end
  endgenerate

  assign mem_read_valid    = core.mem_read_valid;
  assign mem_read_addr     = core.mem_read_addr;
  assign mem_read_memWidth = core.mem_read_memWidth;
  assign mem_write_valid   = core.mem_write_valid;
  assign mem_write_addr    = core.mem_write_addr;
  assign mem_write_data    = core.mem_write_data;
  assign mem_write_memWidth = core.mem_write_memWidth;

  assign event_valid          = core.event_valid;
  assign event_intrNO         = core.event_intrNO;
  assign event_cause          = core.event_cause;
  assign event_exceptionPC    = core.event_exceptionPC;
  assign event_exceptionInst  = core.event_exceptionInst;
endmodule