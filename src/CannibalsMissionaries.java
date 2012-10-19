import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class CannibalsMissionaries {

	/**
	 * @param args
	 * @throws IOException 
	 */
	final Lock lock = new ReentrantLock();
	final Condition m = lock.newCondition();
	final Condition c = lock.newCondition();
	final Condition boatfull = lock.newCondition();
	
	private static int cannibals;
	private static int missionaries;
	private int boatspace = 3;
	private String message = "Boat leaving with ";
	private int cannibalonboat = 0;
	private int missionaryonboat = 0;
	private boolean arranged = false;
	
	
	private int cannibalmax;
	private int missionarymax;
	
	public void cannibalon(String id) throws InterruptedException{
		lock.lock();
		
		try{
			if(!arranged){
				arrange();
			}
			
			if(cannibalmax == 0 && missionaryonboat!=0){
				c.await();
			}
			
//			if(allcannibalspermit&&(cannibalmax==0)&&(missionaryonboat==0)){
//				cannibalonboard(id);
//				allcannibalsure = true;
//			}
//			else {
//				cannibalonboard(id);
//				
//				
//				
//				if(boatspace == 0){
//					boatgo();
//					
//				}
//			}
			if(cannibalmax!=0){
				cannibalonboard(id);
				if(boatspace == 0){
					boatgo();
				}
			} else if (cannibalmax == 0&&missionaryonboat==0){
				cannibalonboard(id);
				missionarymax = 0;
				if(boatspace ==0 ){
					boatgo();
				}
			}
			
			
		}finally{
			lock.unlock();
		}
	}
	
	private void cannibalonboard(String id) {
		// TODO Auto-generated method stub
		cannibalmax = cannibalmax - 1;
		cannibals = cannibals - 1;
		boatspace = boatspace - 1;
		message =  message + "Cannibal"+id+" ";
		cannibalonboat = cannibalonboat+1;
	}

	public void missionaryon(String id) throws InterruptedException{
		lock.lock();
		try{
			
			if(!arranged){
				arrange();
			}
			if(missionarymax == 0){
				m.await();
			} 
				missionarymax = missionarymax - 1;
				missionaries = missionaries - 1;
				boatspace = boatspace - 1;
				message = message + "Missionary" + id+" ";
				missionaryonboat = missionaryonboat + 1;
				
			
			
			
			
			if(boatspace == 0){
				boatgo();
			}
		}finally{
			lock.unlock();
		}
	}
	
	public void boatgo(){	
			System.out.println(message);
			
			boatspace = 3;			
					
			m.signal();
			c.signal();	
			message = "Boat leaving with ";
			cannibalonboat = 0;
			missionaryonboat = 0;
			
			arrange();
			
			
	}
	
	private void arrange() {
		// TODO Auto-generated method stub
		arranged = true;
		
		if(missionaries<=3){
			missionarymax = missionaries;
			cannibalmax = 3-missionarymax;
		} else if(missionaries>3){
			if((missionaries - 2) >= (2*(cannibals-1) % 3)){
				cannibalmax = 1;
			}else{
				cannibalmax = 0;
			}
			
			if((missionaries - 3)>(2*(cannibals%3))){
				missionarymax = 3;
			}else{
				missionarymax = 2;
			}
		}
		
		
		
		
	}

	public class mgeton implements Runnable{

		
		String id;
		public mgeton(String id){
			this.id = id;
		}
		
		@Override
		public void run() {
			
			// TODO Auto-generated method stub
			
			try {
				
				missionaryon(id);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public class cgeton implements Runnable{
		
		String id;
		public cgeton(String id){
			this.id = id;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				cannibalon(id);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		CannibalsMissionaries cm = new CannibalsMissionaries();
		
		StringTokenizer st;
		String numbers = input.readLine();
		st = new StringTokenizer(numbers);
		cannibals = Integer.parseInt(st.nextToken());
		missionaries = Integer.parseInt(st.nextToken());
		
		
		if(cannibals>=3){
			allcannibalspermit = true;
		} else {
			allcannibalspermit = false;
		}
		
		String data = input.readLine();
		while(!data.equals("")){
			
			st = new StringTokenizer(data);
			String species = st.nextToken();
			String idnumber = st.nextToken();
			if(species.equals("Cannibal")){
				new Thread(cm.new cgeton(idnumber)).start();
			} else if(species.equals("Missionary")){
				new Thread(cm.new mgeton(idnumber)).start();
			}
			
			data = input.readLine();
		}
		return ;
	}
	
	

}
