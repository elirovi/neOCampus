package IHM;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import capteur.Capteur;
import capteur.CapteurExterieur;
import capteur.emplacement.Batiment;
import capteur.emplacement.CapteurInterieur;
import capteur.emplacement.Etage;
import capteur.emplacement.Salle;

public class CapteurTreeModel extends DefaultTreeModel{
	private static final long serialVersionUID = 1L;
	List<Batiment> batiments= new ArrayList<>();
	List<CapteurExterieur> capExt = new ArrayList<>();
	
	public CapteurTreeModel(TreeNode root) {
		super(root);
	}
	
	public Object getRoot(){
		return "Capteurs";
	}
	private <T> T getSame(T t, List<T> list){
		for(T tt : list)
			if(tt.equals(t))
				return tt;
		return t;
	}
	public void add(Capteur c){
		if(c instanceof CapteurInterieur){
			CapteurInterieur ci= (CapteurInterieur)c;
			Batiment b =getSame(ci.getBatiment(),batiments);
			Etage e;
			Salle s;
			if(!batiments.contains(b)){
				batiments.add(b);
				e=ci.getEtage();
				b.addEtage(e);
				s= ci.getSalle();
				e.addSalle(s);
			}else{
				e=getSame(ci.getEtage(),b.getEtages());
				if(!b.getEtages().contains(e)){
					b.addEtage(e);
					s= ci.getSalle();
					e.addSalle(s);
				}else{
					s=getSame(ci.getSalle(),e.getSalles());
					if(!e.getSalles().contains(s))
						e.addSalle(s);
				}
			}
			s.addCapteur(ci);
		}else{
			CapteurExterieur ce=(CapteurExterieur)c;
			ce= getSame(ce, capExt);		
			if(!capExt.contains(ce))
				capExt.add(ce);
		}
	}
	private Capteur stringToCap(String id){
		for(Capteur cap:capExt)
			if(cap.getID().equals(id))
				return cap;
		for(Batiment b: batiments)
			for(Etage e: b.getEtages())
				for(Salle s: e.getSalles())
					for(CapteurInterieur cap:s.getCapteurs())
						if(cap.getID().equals(id))
							return cap;
		return null;
	}
	public void remove(String id){
		Capteur c=stringToCap(id);
		if(c!=null)
		if(c instanceof CapteurExterieur)
			capExt.remove((CapteurExterieur)c);
		else{
			CapteurInterieur cap= (CapteurInterieur)c;
			Batiment b= getSame(cap.getBatiment(), batiments);
			Etage e= getSame(cap.getEtage(), b.getEtages());
			Salle s= getSame(cap.getSalle(),e.getSalles());
			s.removeCapteur(cap);
			if(s.listVide()){
				e.removeSalle(s);
				if(e.listVide()){
					b.removeEtage(e);
					if(b.listVide())
						batiments.remove(b);
				}
			}
		}
		//Object[] children={c};
		//fireTreeNodesRemoved(this, new TreePath(c).getParentPath(), , children);
	}

	public Object getChild(Object parent, int index) {
		if(parent instanceof String)
			switch((String)parent){
				case "Capteurs":
					switch(index){
						case 0: return "Interieurs";
						case 1: return "Exterieurs";
						default: return null;
					}
				case "Interieurs":
					if(index>=batiments.size())
						return null;
					return batiments.get(index);
				case "Exterieurs":
					if(index>=capExt.size())
						return null;
					return capExt.get(index);
			}
		if(parent instanceof Batiment){
			if(index>=((Batiment)parent).getEtages().size())
				return null;
			return ((Batiment)parent).getEtages().get(index);
		}
		if(parent instanceof Etage){
			if(index>=((Etage)parent).getSalles().size())
				return null;
			return ((Etage)parent).getSalles().get(index);
		}
		if(parent instanceof Salle){
			if(index>=((Salle)parent).getCapteurs().size())
				return null;
			return ((Salle)parent).getCapteurs().get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if(parent instanceof String)
			switch((String)parent){
				case "Capteurs": return 2;
				case "Interieurs": return batiments.size();
				case "Exterieurs": return capExt.size();
			}
		if(parent instanceof Batiment)
			return ((Batiment)parent).getEtages()==null?0:((Batiment) parent).getEtages().size();
		if(parent instanceof Etage)
			return ((Etage)parent).getSalles().size();
		if(parent instanceof Salle)
			return ((Salle)parent).getCapteurs().size();
		return 0;
	}
	public boolean isLeaf(Object node){
		if(node instanceof String){
			if(node.equals("Interieurs")||node.equals("Exterieurs"))
				return false;
		}
		return getChildCount(node)==0;
    }
	public List<Capteur> getCapteurs(TreePath node){
		List<Capteur> list= new ArrayList<>();
		if(node.getPathCount()==0)return list;
		if(isLeaf(node.getLastPathComponent())){
			list.add((Capteur)node.getLastPathComponent());
			return list;
		}
		Object o= node.getLastPathComponent();
		for(int i=0; i<getChildCount(o); i++){
			List<Capteur> aux= getCapteurs(node.pathByAddingChild(getChild(o,i)));
			for(int j=0;j<aux.size();j++)
				if(!list.contains(aux.get(j)))
					list.add(aux.get(j));
		}
		return list;
	}
}
