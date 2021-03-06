package capteur;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import capteur.emplacement.CapteurInterieur;

public class ServeurCapteur {
	private Socket sock;
	public ServeurCapteur(String adr,int port) throws Exception{
		sock=new Socket(adr,port);
	}
	public void envoyerValeurCapteur(double val) throws Exception{
		PrintStream p=new PrintStream(sock.getOutputStream());
		p.println("ValeurCapteur;"+val);
		p.flush();
	}
	public boolean deconnecterCapteur(String id) throws Exception{
		PrintStream p= new PrintStream(sock.getOutputStream());
		p.println("DeconnexionCapteur;"+id);
		@SuppressWarnings("resource")
		Scanner sc=new Scanner(sock.getInputStream());
		String conf =sc.nextLine();
		if(conf.equals("DeconnexionOK")){
			System.out.println("Deconnexion du capteur "+ id + " reussie");
			return true;
		}else{
			System.out.println("Deconnexion du capteur "+ id + " echouee");
			return false;
		}		
	}
	public boolean envoyerConnexionCapteur(CapteurExterieur ce) throws Exception{
		PrintStream p=new PrintStream(sock.getOutputStream());
		p.println("ConnexionCapteur;"+ce.getID()+";"+ce.getType().toString()+";"+ce.getGPS().getLatitude()+";"+ce.getGPS().getLongitude());
		p.flush();
		@SuppressWarnings("resource")
		Scanner sc=new Scanner(sock.getInputStream());
		String conf =sc.nextLine();
		if(conf.equals("ConnexionOK")){
			System.out.println("Connexion du capteur "+ ce.getID() + " reussie");
			return true;
		}
		else{
			System.out.println("Connexion du capteur "+ ce.getID() + " echouee");
			return false;
		}
	}
	public boolean envoyerConnexionCapteur(CapteurInterieur ci) throws Exception{
		PrintStream p= new PrintStream(sock.getOutputStream());
		p.println("ConnexionCapteur;"+ci.getID()+";"+ci.getType().toString()+";"+ci.getBatiment()+";"+ci.getEtage().get()+";"+ci.getSalle()+";"+ci.getPosition());
		@SuppressWarnings("resource")
		Scanner sc=new Scanner(sock.getInputStream());
		String conf =sc.nextLine();
		if(conf.equals("ConnexionOK")){
			System.out.println("Connexion du capteur "+ ci.getID() + " reussie");
			return true;
		}
		else{
			System.out.println("Connexion du capteur "+ ci.getID() + " echouee");
			return false;
		}
	}
}