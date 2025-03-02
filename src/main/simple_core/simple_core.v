module simple_cpu(
    input wire clk,          // 时钟信号
    input wire reset,        // 复位信号
    output reg [7:0] pc      // 程序计数器
);

    // 定义指令操作码
    localparam LOAD  = 4'b0001;
    localparam STORE = 4'b0010;
    localparam ADD   = 4'b0011;
    localparam JUMP  = 4'b0100;

    // 定义寄存器
    reg [7:0] R0; // 寄存器 R0
    reg [7:0] R1; // 寄存器 R1

    // 定义内存
    reg [7:0] memory [0:15]; // 16 个 8 位内存单元

    // 定义指令寄存器
    reg [7:0] ir; // 指令寄存器

    // 提取操作码和操作数
    wire [3:0] opcode = ir[7:4]; // 操作码
    wire [3:0] operand = ir[3:0]; // 操作数

    // 程序计数器逻辑
    always @(posedge clk or posedge reset) begin
        if (reset) begin
            pc <= 8'b0; // 复位时 PC 置 0
        end else begin
            case (opcode)
                JUMP: pc <= operand; // 跳转指令
                default: pc <= pc + 1; // 其他指令，PC + 1
            endcase
        end
    end

    // 指令执行逻辑
    always @(posedge clk) begin
        if (!reset) begin
            ir <= memory[pc]; // 取指令
            case (opcode)
                LOAD: R0 <= memory[operand]; // 加载指令
                STORE: memory[operand] <= R0; // 存储指令
                ADD: R0 <= R0 + R1; // 加法指令
                JUMP: ; // 跳转指令已在 PC 逻辑中处理
                default: ; // 默认情况
            endcase
        end
    end

endmodule