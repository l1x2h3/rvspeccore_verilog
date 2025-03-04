`include "CheckerWithResult.v" 

module TopModule (
  input         clock,
  input         reset,
  input         instCommit_valid, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [31:0] instCommit_inst, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] instCommit_pc, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_0, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_1, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_2, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_3, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_4, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_5, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_6, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_7, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_8, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_9, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_10, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_11, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_12, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_13, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_14, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_15, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_16, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_17, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_18, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_19, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_20, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_21, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_22, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_23, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_24, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_25, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_26, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_27, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_28, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_29, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_30, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_reg_31, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_pc, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_misa, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mvendorid, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_marchid, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mimpid, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mhartid, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mstatus, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mstatush, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mscratch, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mtvec, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mcounteren, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_medeleg, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mideleg, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mip, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mie, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mepc, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mcause, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_mtval, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_cycle, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_scounteren, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_scause, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_stvec, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_sepc, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_stval, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_sscratch, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_satp, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_pmpcfg0, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_pmpcfg1, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_pmpcfg2, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_pmpcfg3, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_pmpaddr0, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_pmpaddr1, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_pmpaddr2, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] result_csr_pmpaddr3, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [7:0]  result_csr_MXLEN, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [7:0]  result_csr_IALIGN, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [7:0]  result_csr_ILEN, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [1:0]  result_internal_privilegeMode, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input         event_valid, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] event_intrNO, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] event_cause, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] event_exceptionPC, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] event_exceptionInst, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input         mem_read_valid, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] mem_read_addr, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [6:0]  mem_read_memWidth, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] mem_read_data, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input         mem_write_valid, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] mem_write_addr, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [6:0]  mem_write_memWidth, // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
  input  [63:0] mem_write_data // @[src/main/scala/rvspeccore/checker/Checker.scala 63:16]
);

  
    CheckerWithResult checker_instance (
        .clock                              (clock),
        .reset                              (reset),
        .io_instCommit_valid                (io_instCommit_valid),
        .io_instCommit_inst                 (io_instCommit_inst),
        .io_instCommit_pc                   (io_instCommit_pc),
        .io_result_pc                       (io_result_pc),
        .io_result_reg_0,                   (result_reg_0,),                   
        .io_result_reg_1,                   (result_reg_1,),                   
        .io_result_reg_2,                   (result_reg_2,),                   
        .io_result_reg_3,                   (result_reg_3,),                   
        .io_result_reg_4,                   (result_reg_4,),                   
        .io_result_reg_5,                   (result_reg_5,),                   
        .io_result_reg_6,                   (result_reg_6,),                   
        .io_result_reg_7,                   (result_reg_7,),                   
        .io_result_reg_8,                   (result_reg_8,),                   
        .io_result_reg_9,                   (result_reg_9,),                   
        .io_result_reg_10                   (result_reg_10),                   
        .io_result_reg_11                   (result_reg_11),                   
        .io_result_reg_12                   (result_reg_12),                   
        .io_result_reg_13                   (result_reg_13),                   
        .io_result_reg_14                   (result_reg_14),                   
        .io_result_reg_15                   (result_reg_15),                   
        .io_result_reg_16                   (result_reg_16),                   
        .io_result_reg_17                   (result_reg_17),                   
        .io_result_reg_18                   (result_reg_18),                   
        .io_result_reg_19                   (result_reg_19),                   
        .io_result_reg_20                   (result_reg_20),                   
        .io_result_reg_21                   (result_reg_21),                   
        .io_result_reg_22                   (result_reg_22),                   
        .io_result_reg_23                   (result_reg_23),                   
        .io_result_reg_24                   (result_reg_24),                   
        .io_result_reg_25                   (result_reg_25),                   
        .io_result_reg_26                   (result_reg_26),                   
        .io_result_reg_27                   (result_reg_27),                   
        .io_result_reg_28                   (result_reg_28),                   
        .io_result_reg_29                   (result_reg_29),                   
        .io_result_reg_30                   (result_reg_30),                   
        .io_result_reg_31                   (result_reg_31),                   
        .io_result_pc                       (result_pc             ),            
        .io_result_csr_misa                 (result_csr_misa       ),    
        .io_result_csr_mvendorid            (result_csr_mvendorid  ),    
        .io_result_csr_marchid              (result_csr_marchid    ),    
        .io_result_csr_mimpid               (result_csr_mimpid     ),    
        .io_result_csr_mhartid              (result_csr_mhartid    ),    
        .io_result_csr_mstatus              (result_csr_mstatus    ),    
        .io_result_csr_mstatush             (result_csr_mstatush   ),    
        .io_result_csr_mscratch             (result_csr_mscratch   ),    
        .io_result_csr_mtvec                (result_csr_mtvec      ),    
        .io_result_csr_mcounteren           (result_csr_mcounteren ),    
        .io_result_csr_medeleg              (result_csr_medeleg    ),    
        .io_result_csr_mideleg              (result_csr_mideleg    ),    
        .io_result_csr_mip                  (result_csr_mip        ),    
        .io_result_csr_mie                  (result_csr_mie        ),    
        .io_result_csr_mepc                 (result_csr_mepc       ),    
        .io_result_csr_mcause               (result_csr_mcause     ),    
        .io_result_csr_mtval                (result_csr_mtval      ),    
        .io_result_csr_cycle                (result_csr_cycle      ),    
        .io_result_csr_scounteren           (result_csr_scounteren ),    
        .io_result_csr_scause               (result_csr_scause     ),    
        .io_result_csr_stvec                (result_csr_stvec      ),    
        .io_result_csr_sepc                 (result_csr_sepc       ),    
        .io_result_csr_stval                (result_csr_stval      ),    
        .io_result_csr_sscratch             (result_csr_sscratch   ),    
        .io_result_csr_satp                 (result_csr_satp       ),    
        .io_result_csr_pmpcfg0              (result_csr_pmpcfg0    ),    
        .io_result_csr_pmpcfg1              (result_csr_pmpcfg1    ),    
        .io_result_csr_pmpcfg2              (result_csr_pmpcfg2    ),    
        .io_result_csr_pmpcfg3              (result_csr_pmpcfg3    ),    
        .io_result_csr_pmpaddr0             (result_csr_pmpaddr0   ),    
        .io_result_csr_pmpaddr1             (result_csr_pmpaddr1   ),    
        .io_result_csr_pmpaddr2             (result_csr_pmpaddr2   ),    
        .io_result_csr_pmpaddr3             (result_csr_pmpaddr3   ),    
        .io_result_csr_MXLEN                (result_csr_MXLEN      ),    
        .io_result_csr_IALIGN               (result_csr_IALIGN     ),    
        .io_result_csr_ILEN                 (result_csr_ILEN       ),    
        .io_result_internal_privilegeMode   (result_internal_privilegeMode),                       
        .io_event_valid                     (event_valid                  ),                    
        .io_event_intrNO                    (event_intrNO                 ),                    
        .io_event_cause                     (event_cause                  ),                    
        .io_event_exceptionPC               (event_exceptionPC            ),                    
        .io_event_exceptionInst             (event_exceptionInst          ),                    
        .io_mem_read_valid                  (mem_read_valid               ),                    
        .io_mem_read_addr                   (mem_read_addr                ),                    
        .io_mem_read_memWidth               (mem_read_memWidth            ),                    
        .io_mem_read_data                   (mem_read_data                ),                    
        .io_mem_write_valid                 (mem_write_valid              ),                    
        .io_mem_write_addr                  (mem_write_addr               ),                    
        .io_mem_write_memWidth              (mem_write_memWidth           ),                        
        .io_mem_write_data                  (mem_write_data               ),                     
    );

endmodule