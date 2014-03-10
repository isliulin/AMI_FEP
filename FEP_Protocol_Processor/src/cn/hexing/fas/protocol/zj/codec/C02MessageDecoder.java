package cn.hexing.fas.protocol.zj.codec;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import cn.hexing.exception.MessageDecodeException;
import cn.hexing.fas.model.RtuData;
import cn.hexing.fas.model.RtuDataItem;
import cn.hexing.fas.protocol.Protocol;
import cn.hexing.fas.protocol.codec.MessageCodecContext;
import cn.hexing.fas.protocol.conf.ProtocolDataItemConfig;
import cn.hexing.fas.protocol.data.DataItem;
import cn.hexing.fas.protocol.data.DataMappingZJ;
import cn.hexing.fas.protocol.gw.parse.DataSwitch;
import cn.hexing.fas.protocol.meter.IMeterParser;
import cn.hexing.fas.protocol.meter.MeterParserFactory;
import cn.hexing.fas.protocol.zj.parse.DataItemParser;
import cn.hexing.fas.protocol.zj.parse.ParseTool;
import cn.hexing.fas.protocol.zj.parse.TaskSetting;
import cn.hexing.fas.protocol.zj.parse.ZjDateAssistant;
import cn.hexing.fk.message.IMessage;
import cn.hexing.fk.message.zj.MessageZj;
import cn.hexing.fk.model.MeasuredPoint;
import cn.hexing.fk.utils.HexDump;
import cn.hexing.fk.utils.StringUtil;
/**
 * ����������(�����룺02H)��Ӧ��Ϣ������
 *
 */
public class C02MessageDecoder extends AbstractMessageDecoder{	
	private static final Logger log = Logger.getLogger(C02MessageDecoder.class);

	public Object decode(IMessage message) {
        List<RtuData> datas = new ArrayList<RtuData>();
		try{
			if(getOrientation(message)==DataMappingZJ.ORIENTATION_TO_APP){	//���ն�Ӧ��
				int rtype=(getErrCode(message));			
				if(rtype==DataMappingZJ.ERROR_CODE_OK){	//�����ն�Ӧ��
					//ȡӦ������
        			byte[] data=getData(message);
                    
        			if(data.length>9){	//����9�ֽ����ݣ�1�ֽ������+6�ֽ���ʼʱ��+1�ֽ�ʱ������λ+1�ֽ�ʱ����ֵ��        				
        				String tasknum=String.valueOf(data[0] & 0xff);
        				TaskSetting ts=new TaskSetting(((MessageZj)message).head.rtua,(data[0] & 0xff),super.dataConfig);        				
        				if(ts!=null && ts.getRtask()!=null && ts.getDataNum()>0){	//�ն��������û�ȡ��ȷ
        					Calendar time=getTime(data,1);
        					if(time==null){
        						throw new MessageDecodeException("֡�а�����ʱ�����ݴ���:"+ParseTool.BytesToHex(data,1,5));
        					}
        					
        					//ʱ���жϣ�������ն��Զ��ϱ����ҽ��ܱ���ʱ���ǵ�ǰʱ�䣬
        					//�������������ʱ���뵱ǰʱ���6Сʱ����ʱ�Ӵ���Ӧ������12Сʱ�ڲ��жϣ����Է�ֹ���ػ�ѹ

        					int pnum=data[6] & 0xFF;	//���ݵ���
        					int ti=getTimeInterval(data[7]);	//���ݵ�ʱ��������
        					int tn=data[8] & 0xFF;		//���ݵ�ʱ����ֵ
        					
        					if(ts.getTT()==2){
        						parseRelayTask(data,9,time,tasknum,ts,datas);
        					}else{        						
        						//�������
        						if((data.length-9)==(ts.getDataLength()*pnum)){        						            						
            						List datacs=ts.getDI();
            						int loc=9;
            						Hashtable<String,String> keys=new Hashtable<String,String>();//��ֹ�ظ������� by yangdh---2007/02/06
            						for(int i=0;i<pnum;i++){	//ѭ��ÿ�����ݵ�
            							RtuData bean = new RtuData();
            							RtuDataItem beanItem=null;
            							keys.clear();						
            							for(int j=0;j<ts.getDataNum();j++){	//ѭ��ÿ��������
            								ProtocolDataItemConfig pdc=(ProtocolDataItemConfig)datacs.get(j);            								
            								List childs=pdc.getChildItems();
            								if(childs!=null && childs.size()>0){
            									for(Iterator iterc=childs.iterator();iterc.hasNext();){
            										ProtocolDataItemConfig cpdc=(ProtocolDataItemConfig)iterc.next();
            										Object di=DataItemParser.parsevalue(data,loc,cpdc.getLength(),cpdc.getFraction(),cpdc.getParserno());        	    								
        	        								loc+=cpdc.getLength();       	        								    	                                                    	                                           
    	                                            if(!keys.containsKey(cpdc.getCode())){
    	                                            	beanItem=new RtuDataItem();
    	                                            	beanItem.setCode(cpdc.getCode());
    	                                            	beanItem.setValue((di==null)?null:di.toString());  
    	                                            	bean.addDataList(beanItem);
    	                                            	keys.put(cpdc.getCode(), cpdc.getCode());
    	                                            }   
    	                                            bean.setLogicAddress(ts.getRtu().getLogicAddress());    	                                                    	                                        
        	                                        bean.setTaskNum(tasknum);        	                                      
        	                                        bean.setTime(time.getTime());
            									}
            								}else{
            									if(pdc.getCode().equals("C117")){
            										byte [] timeData = new byte[6];
            										System.arraycopy(data, loc, timeData, 0, 6);
    	        									time=getTime(HexDump.toArray(DataSwitch.ReverseStringByByte(HexDump.toHex(timeData))), 0);
    	        									//�����C117,�����¶���ʱ��
    	        								}else{
    	        									Object di=DataItemParser.parsevalue(data,loc,pdc.getLength(),pdc.getFraction(),pdc.getParserno());   	        								
    	                                            if(!keys.containsKey(pdc.getCode())){	                                            	
    	                                            	beanItem=new RtuDataItem();
    	                                            	beanItem.setCode(pdc.getCode());
    	                                            	beanItem.setValue((di==null)?null:di.toString());  
    	                                            	bean.addDataList(beanItem);
    	                                            	keys.put(pdc.getCode(), pdc.getCode());
    	                                            }
    	        								}
    	        								loc+=pdc.getLength(); 
	                                            bean.setLogicAddress(ts.getRtu().getLogicAddress());	                                            
	                                            bean.setTime(time.getTime());
	                                            bean.setTaskNum(tasknum);
            								}
            							}  
            							datas.add(bean);
            							if(ti<=1440){
            								time.add(Calendar.MINUTE,ti*tn);
            							}else{
            								//ʱ����Ϊ��
            								time.add(Calendar.MONTH,tn);
            							}
            						}        						
            					}else{
            						MessageCodecContext.setTaskNum(tasknum);
            						String msg="�ն��߼���ַ��"+ParseTool.IntToHex4(((MessageZj)message).head.rtua)+"������ţ�"+tasknum;
            						msg+="\r\n"+"���ݳ��Ȳ���,�������ݳ��ȣ�"+(ts.getDataLength()*pnum+9)+"  �ϱ����ݳ��ȣ�"+data.length;
            						throw new MessageDecodeException(msg);
            					}
        					}        					
        				}else{
        					//δ֪������
        					MessageCodecContext.setTaskNum(tasknum);
        					throw new MessageDecodeException("δ֪������");
        				}
        			}
        			else{
        				String msg="�ն��߼���ַ��"+ParseTool.IntToHex4(((MessageZj)message).head.rtua)+"�������ݳ��ȷǷ�";   						
                        throw new MessageDecodeException(msg);
        			}
        		}else{
        		}
			}else{				
				String msg="�ն��߼���ַ��"+ParseTool.IntToHex4(((MessageZj)message).head.rtua)+"�������ݸ�ʽ�Ƿ�";   						
                throw new MessageDecodeException(msg);
			}
		}catch(Exception e){
			throw new MessageDecodeException(e);
		}
		return datas;
	}
    
   /* private RtuData getBean(String beanClass, int tn, int pnum, List datas, Map<String,Object> beanMap) {
        String key = beanClass + "." + tn + "." + pnum;
        
        Object bean = beanMap.get(key);
        if (bean == null) {
        
            Class clazz;
            try {
                clazz = Class.forName(beanClass);
                bean = clazz.newInstance();
            }
            catch (Exception ex) {
                throw new RuntimeException("Error to instantiating bean: " + beanClass, ex);
            }
            
            datas.add(bean);
            beanMap.put(key, bean);
        }
        
        return (RtuData) bean;
    }*/
	
	/**
     * ȡ���䷽��
     * @param message
     * @return 0����վ�·� 1���ն�Ӧ��
     */
    private int getOrientation(IMessage message){    	
    	return ParseTool.getOrientation(message);
    }
    
    /**
     * ȡ�������
     * @param message
     * @return
     */
    private int getErrCode(IMessage message){    	
    	return ParseTool.getErrCode(message);
    }
    
    /**
     * ȡ��Ϣ������
     * @param message
     * @return
     */
    private byte[] getData(IMessage message){
    	return ParseTool.getData(message);
    }
        
    /**
     * ��������ʱ��
     * @param data
     * @param offset
     * @return
     */
    private Calendar getTime(byte[] data,int offset){
    	Calendar rt=null;
    	try{
    		int month=ParseTool.BCDToDecimal(data[1+offset]);
    		int year=ParseTool.BCDToDecimal(data[offset]);
    		if(month>0 && year>=0){
	    		if(ParseTool.isValidMonth(data[1+offset]) && ParseTool.isValidDay(data[2+offset],month,year+2000)
	    				&& ParseTool.isValidHHMMSS(data[3+offset]) && ParseTool.isValidHHMMSS(data[4+offset]))
	    		{		
	    			String strIsIran = System.getProperty("bp.isIranTime");
					boolean isIran = strIsIran==null || new Boolean(strIsIran)==false?false:true;
			    	rt=Calendar.getInstance();		    		
	    	
			    	int num=ParseTool.BCDToDecimal((byte)(data[2+offset] & 0x3f));
			    	int day = num;
					if(isIran){
						//����������
						String time=(year+2000)+"-"+(month>10?month:"0"+month)+"-"+(day>10?day:"0"+day)+" 00:00:00";
						time=ZjDateAssistant.upDateTimeProcess(time, "yyyy-MM-dd");
						String dates[]=time.split("-");
						year = Integer.parseInt(dates[0].substring(2));
						month = Integer.parseInt(dates[1]);
						day = Integer.parseInt(dates[2]);
					}
			    	rt.set(Calendar.YEAR,year+2000);			    	
			    	rt.set(Calendar.MONTH,month-1);		
			    	rt.set(Calendar.DAY_OF_MONTH,day);
			    	num=ParseTool.BCDToDecimal((byte)(data[3+offset] & 0x3f));
			    	rt.set(Calendar.HOUR_OF_DAY,num);
			    	num=ParseTool.BCDToDecimal((byte)(data[4+offset] & 0x7f));
			    	if(num>=60){
			    		rt.add(Calendar.HOUR_OF_DAY,1);
			    		num=0;
			    	}
			    	rt.set(Calendar.MINUTE,num);
			    	rt.set(Calendar.SECOND,0);
			    	rt.set(Calendar.MILLISECOND,0);
	    		}
    		}
    	}catch(Exception e){
    		log.error(StringUtil.getExceptionDetailInfo(e));
    	}    	
    	return rt;
    }
    
    /**
     * ʱ������ͳһ��λΪ�֣��±Ƚ����⣬�������������������̣�
     * @param type
     * @return
     */
    private int getTimeInterval(byte type){
    	int rt=0;
    	switch(type){
	    	case 2:
	    		rt=1;
	    		break;
	    	case 3:
	    		rt=60;
	    		break;
	    	case 4:
	    		rt=1440;
	    		break;
	    	case 5:
	    		rt=43200;
	    		break;
	    	default:
	    		break;
    	}    	
    	return rt;
    }
    
    private void parseRelayTask(byte[] data,int pos,Calendar time,String tasknum,TaskSetting ts,List<RtuData> result){
    	if(data!=null){
    		//ȡ�������Լ����
    		MeasuredPoint mp=ts.getRtu().getMeasuredPoint(String.valueOf(ts.getTN()));
    		if(mp==null){
        		throw new MessageDecodeException("ָ�������㲻���ڣ��ն�--"+ts.getRtu().getLogicAddress()+"  ������--"+ts.getTN());
        	}
        	IMeterParser mparser=MeterParserFactory.getMeterParser(Protocol.BBMeter97);        	
    		//������֡
        	Object[] dis=mparser.parser(data,pos,data.length-pos);
        	if(dis!=null){
        		RtuData bean = new RtuData();
        		RtuDataItem beanItem=null;
        		for(int i=0;i<dis.length;i++){
        			DataItem di=(DataItem)dis[i]; 
                    beanItem=new RtuDataItem();
                	beanItem.setCode((String)di.getProperty("datakey"));
                	beanItem.setValue((di==null)?null:di.toString());  
                	bean.addDataList(beanItem);                  	
                    bean.setLogicAddress(ts.getRtu().getLogicAddress());                                        
                    bean.setTaskNum(tasknum);                    
                    bean.setTime(time.getTime());
                    /*
                    bean.setTn(String.valueOf(ts.getTN()));
                    bean.setTaskProperty(ts.getTaskProperty());
                    bean.setDeptCode(ts.getDeptCode());
                    bean.setCt(ts.getCt());
                    bean.setPt(ts.getPt());*/
                    result.add(bean);
        		}
        	}
    	}
    }
}