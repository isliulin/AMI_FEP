package cn.hexing.tool;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;
import cn.hexing.fk.utils.HexDump;
import cn.hexing.tool.decriptor.Decriptor;
import cn.hexing.tool.explain.DecoderMainFrame;

import com.hx.dlms.applayer.CosemObis;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class Tools extends javax.swing.JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates new form Tools
     */
    public Tools() {
        initComponents();
    }
    
    private String[] stringOperation = new String[]{"分割帧","去掉空格","Ascii2String","String2Ascii","HexToDec","DecToHex"};
    private OPERATION[] operations = new OPERATION[]{OPERATION.SPLIT_FRAME,OPERATION.REMOVE_SPACE,OPERATION.ASCII2STRING,OPERATION.STRING2ASCII,OPERATION.HEXTODEC,OPERATION.DECTOHEX};

    public enum OPERATION  {SPLIT_FRAME,REMOVE_SPACE,ASCII2STRING,STRING2ASCII,HEXTODEC,DECTOHEX};
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        t_rawFrame = new javax.swing.JTextField(50);
        jLabel2 = new javax.swing.JLabel();
        t_afterProcess = new javax.swing.JTextField(50);
        b_splitFrame = new javax.swing.JButton();
        b_clear = new javax.swing.JButton();
        b_removeSpace = new javax.swing.JButton();
        b_callExplain = new javax.swing.JButton();
        b_callDecriptor = new javax.swing.JButton();
        b_callSysCalc = new javax.swing.JButton();
        b_ascii2String = new javax.swing.JButton();
        b_string2ascii = new javax.swing.JButton();
        b_hexToOct = new javax.swing.JButton();
        b_octToHex = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("原始帧：");

        jLabel2.setText("处理后：");

        b_splitFrame.setText("分割帧");
        b_splitFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_processFrameActionPerformed(evt);
            }
        });

        b_clear.setText("清空");
        b_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_clearActionPerformed(evt);
            }
        });

        b_removeSpace.setText("去掉空格");
        b_removeSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	b_processFrameActionPerformed(evt);
            }
        });

        b_callExplain.setText("调用解析程序");
        b_callExplain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_callExplainActionPerformed(evt);
            }
        });

        b_callDecriptor.setText("调用解密程序");
        b_callDecriptor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_callDecriptorActionPerformed(evt);
            }
        });

        b_callSysCalc.setText("调用系统计算器");
        b_callSysCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_callSyscalcActionPerformed(evt);
            }
        });

        b_ascii2String.setText("Ascii2String");
        b_ascii2String.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	b_processFrameActionPerformed(evt);
            }
        });

        b_string2ascii.setText("String2Ascii");
        b_string2ascii.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	b_processFrameActionPerformed(evt);
            }
        });

        b_hexToOct.setText("HexToDec");
        b_hexToOct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	b_processFrameActionPerformed(evt);
            }
        });

        b_octToHex.setText("DecToHex");
        b_octToHex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	b_processFrameActionPerformed(evt);
            }
        });

        getContentPane().setLayout(new MigLayout("","",""));
        getContentPane().add(jLabel1,"split 2");
        getContentPane().add(t_rawFrame);
        getContentPane().add(b_splitFrame);
        getContentPane().add(b_removeSpace,"wrap,span 3");
        getContentPane().add(jLabel2,"split 2");
        getContentPane().add(t_afterProcess);
        getContentPane().add(b_ascii2String);
        getContentPane().add(b_string2ascii,"wrap,span 3");
        getContentPane().add(b_callExplain,"split 2");
        getContentPane().add(b_callDecriptor);
        getContentPane().add(b_callSysCalc);
        getContentPane().add(b_clear);


        pack();
      }// </editor-fold>//GEN-END:initComponents

	protected void b_callDecriptorActionPerformed(ActionEvent evt) {
		Decriptor.main(null);
	}

	protected void b_callExplainActionPerformed(ActionEvent evt) {
		DecoderMainFrame.main(null);
	}

	private void b_processFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_processFrameActionPerformed
        try {
			String rawFrame=t_rawFrame.getText();
			if(checkStringIsNotNull(rawFrame)){
			    //处理字符串            
				//处理后的字符串，放在结果域里            
				OPERATION index =null;
			    for(int i=0;i<stringOperation.length;i++){
			        if(stringOperation[i].equals(evt.getActionCommand())){
			            index = operations[i];
			            break;
			        }
			    }
			    switch(index){
			        case SPLIT_FRAME:
			        	rawFrame=rawFrame.replaceAll(" ", "");
			        	StringBuffer sb = new StringBuffer(rawFrame.length());
			        	for(int i = 0;i<rawFrame.length()-1;i+=2){
			        		sb.append(rawFrame.subSequence(i, i+2)).append(" ");
			        	}
			        	sb.deleteCharAt(sb.length()-1);
			            t_afterProcess.setText(sb.toString());
			            break;
			        case REMOVE_SPACE:
			        	rawFrame=rawFrame.replaceAll(" ", "");
			            t_afterProcess.setText(rawFrame);
			            break;
			        case ASCII2STRING: //Ascii2String
			        	rawFrame=rawFrame.replaceAll(" ", "");
			        	t_afterProcess.setText(new String(HexDump.toArray(rawFrame)));
			        	break;
			        case STRING2ASCII: //String2Ascii
			        	t_afterProcess.setText(HexDump.hexDump(HexDump.toByteBuffer(HexDump.toHex(rawFrame.getBytes()))));
			        	break;
			        case HEXTODEC: //HexToDec
			        	//0100010800FF
						rawFrame = CosemObis.OidToString(HexDump.toArray(rawFrame));
						t_afterProcess.setText(rawFrame);
			        	break;
			        case DECTOHEX: //DecToHex
			        	//obis转换
						byte[] obis = null;
						try {
							obis = CosemObis.parseObis(rawFrame);
						} catch (Exception e) {
							showMsg("Format Obis like 0-0:11.0.0.255 or 0.0.11.0.0.255");
							return ;
						}
			        	t_afterProcess.setText(HexDump.toHex(obis));
			        	break;
			    }
			    showMsg("已经处理后的帧复制到剪贴板.帧字节数:"+rawFrame.length()/2);
			  //将解析后的数据复制到了剪贴板
			    Toolkit toolkit = Toolkit.getDefaultToolkit();
			    Clipboard clipboard = toolkit.getSystemClipboard();
			    StringSelection stringSel = new StringSelection(t_afterProcess.getText());
			    clipboard.setContents(stringSel, null);
			}else{
			    showMsg("要处理的帧不能为空");
			}
		} catch (HeadlessException e) {
			showMsg("出错!");
		}
        
        
    }//GEN-LAST:event_b_processFrameActionPerformed

    private void showMsg(String msg){
    	JOptionPane.showMessageDialog(this, msg);
    }
    
    private void b_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_clearActionPerformed
        //TODO: 清空数据
    	t_afterProcess.setText(null);
    	t_rawFrame.setText(null);
    }//GEN-LAST:event_b_clearActionPerformed

    private void b_callSyscalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_syscalcActionPerformed
        //TODO: 调用系统计算器   
    	Runtime r = Runtime.getRuntime();
    	try {
			r.exec("calc");
		} catch (IOException e) {
			showMsg("调用系统计算器出错");
		}
    	
    }//GEN-LAST:event_b_syscalcActionPerformed
    /**
     * 判断字符串是否为空
     * @param value
     * @return 
     */
    public boolean checkStringIsNotNull(String value){
        if(value==null || "".equals(value.trim())) return false;
        return true;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Tools.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tools.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tools.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tools.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
		        final Tools tools =new Tools();

            	tools.setVisible(true);
            	Toolkit kit = Toolkit.getDefaultToolkit();    // 定义工具包
                Dimension screenSize = kit.getScreenSize();   // 获取屏幕的尺寸
                int screenWidth = screenSize.width/2;         // 获取屏幕的宽
                int screenHeight = screenSize.height/2;       // 获取屏幕的高
                int height = tools.getHeight();
                int width = tools.getWidth();
                tools.setLocation(screenWidth-width/2, screenHeight-height/2);
            
			}
		});
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b_ascii2String;
    private javax.swing.JButton b_callDecriptor;
    private javax.swing.JButton b_callExplain;
    private javax.swing.JButton b_callSysCalc;
    private javax.swing.JButton b_clear;
    private javax.swing.JButton b_hexToOct;
    private javax.swing.JButton b_octToHex;
    private javax.swing.JButton b_removeSpace;
    private javax.swing.JButton b_splitFrame;
    private javax.swing.JButton b_string2ascii;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField t_afterProcess;
    private javax.swing.JTextField t_rawFrame;
    // End of variables declaration//GEN-END:variables
}