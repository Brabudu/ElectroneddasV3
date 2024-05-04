package main;

public class Stracasciu {
	private String name;
	public static final byte SIZE=20;
	//int actual=0;
	private boolean modified;
	
	
	private Cuntzertu[] stracasciu=new Cuntzertu[SIZE];
	
	public Stracasciu(String name) {
		this.name=name;
		for (int i=0;i<SIZE;i++) {
			stracasciu[i]=new Cuntzertu();
			
		}
		modified=false;
	}
	
	public void poniCuntzertu(int n,Cuntzertu c) {
		stracasciu[n]=Cuntzertu.clone(c);
		modified=true;
		
	}
	public Cuntzertu pigaCuntzertu(int n) {
		//actual=n;
		return Cuntzertu.clone(stracasciu[n]);
	}
	public Cuntzertu getCuntzertu(int n) {
		//actual=n;
		return stracasciu[n];
	}
	public void setCuntzertu(int n,Cuntzertu c) {
		stracasciu[n]=c;
		
	}
	
	public void importStracasciu() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isModified() {
		return modified;
		
	}
	
}
