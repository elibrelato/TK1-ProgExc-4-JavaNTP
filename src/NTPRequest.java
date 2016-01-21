import java.io.Serializable;


public class NTPRequest implements Serializable{
	/**
	 * 	T1 = the time the client sends his/her request to the server
	 * 	T2 = the time the server accepts a request from a client
	 * 	T3 = the time the server finishes processing the request 
	 * 			and send it back to the client (artificial delay of 1200ms)
	 * 	T4 = the time the client receives a response from the server
	 * 
	 * 	Delay (d) is calculated by the total time the message takes transferring back and forth between
	 * 		the client and the server in a measurement
	 * 		d = T2 - T1 + T4 - T3
	 * 
	 * 	Offset (o) is equal to : 0.5 * (T2 - T1 + T3 - T4)
	 */
	private static final long serialVersionUID = 1L;
	
	public long t1;
	public long t2;
	public long t3;
	public long t4;
	public double o;
	public double d;

	public NTPRequest() {
	
	}
	
	public long getT1() {
		return t1;
	}
	public void setT1(long t1) {
		this.t1 = t1;
	}
	public long getT2() {
		return t2;
	}
	public void setT2(long t2) {
		this.t2 = t2;
	}
	public long getT3() {
		return t3;
	}
	public void setT3(long t3) {
		this.t3 = t3;
	}
	public long getT4() {
		return t4;
	}
	public void setT4(long t4) {
		this.t4 = t4;
	}
	
	public void calculateOandD() {
		////
		d = t2-t1 + t4-t3;
		o = 0.5 * (t2-t1+t3-t4);
	}

	public double getD(){
		return d;
	}
	
	public double getO(){
		return o;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "T1="+t1+" | T2="+t2+" | T3="+t3+" | T4="+t4+"\nDelay= "+String.format("%.4f", d)
				+" | Offset= "+String.format("%.4f", o);
	}
	
	
}
