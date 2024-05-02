package main;

import com.google.gson.Gson;

public class Cuntzertu {
	
	public static Cuntzertu clone(Cuntzertu c) {
		Cuntzertu res=new Cuntzertu();
		Gson gson = new Gson();
		
		res=gson.fromJson(gson.toJson(c),c.getClass());
		//System.out.println("Clone "+c.nome+" : "+c.toString()+" > "+res.toString());
		return res;
	}
	
	public static class Preferences {
		public float vol;
		public byte sprogZ;
		public byte sprogS;
		public byte gateMode;
		public byte filterMode;
		
		public float revVol;
		public float revDamp;
		public float revRoom;
		
		public int preferredCuntz;

	}
	
	public static Preferences prefs=new Preferences();		
	
	public class Biquad {
		public int  freq=1000;
		public float q=1;
		public char type='N';
		
		public int mult=0;
	
		public Biquad() {};
		public Biquad(int f, float q, char t) {
			freq=f;
			this.q=q;
			type=t;
			mult=0;
		}
	}
	
	public class Crai {
		public float vol=1;
		public float volA=1;
		public float fini=1;
		public float duty=0.2f;
		public byte puntu=0;
	    Biquad bq=new Biquad();
	    
	    public Biquad getBQ() { return bq; };
	}
	
	public class Canna {
		public Canna(int ncrais) {
			crais=new Crai[ncrais];
			for (int i=0;i<ncrais;i++) {
				crais[i]=new Crai();
			}
			obFactDuty=1;
			obFactVol=1;
		}
		public float volArm;
		public byte strb;
		public byte sonu;
		public byte port;
		public byte timbru;
		
		public float obFactDuty;
		public float obFactVol;
		     
		    Biquad  bqStat=new Biquad();
		    Biquad  bqDinF=new Biquad();
		    Crai [] crais;
		    
		    public Biquad getSBQ() { return bqStat; };
		    public Biquad getDBQ() { return bqDinF; };
		    public Crai getCrai(int n) { return crais[n]; };
		    public int getNCrais() {return crais.length; };
	}
	
	
		public String nome;
	    public String descr;
	    
	    public float vol;
	    public byte cuntz=-1;	//Non inizializzato
	    public byte mod;
	    public byte puntu;
	    public float fini;
	    
	    public byte num_acordadura;

	    public float volT;
	    public float bilT;
	    
	    public float volMs;
	    public float bilMs;
	    
	    public float volMd;
	    public float bilMd;
	    
	   public float revVol=0;
	    public float revDamp=1;
	    public float revRoom=0;
	    
	    public byte ssens;
	    public byte szero;
	    public byte slim;
	    
	    public Biquad bq=new Biquad();
	    
	    public Canna tumbu=new Canna(1);
	    public Canna mancs=new Canna(5);
	    public Canna mancd=new Canna(5);
	    
	    public Biquad getBQ() { return bq; };
	    public boolean isVoid() {return (cuntz==-1) ;}

}
