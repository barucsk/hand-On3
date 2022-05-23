//package examples.clips;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import net.sf.clipsrules.jni.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class dell {

	public static void main(String s[]) throws Exception {
		Environment clips;

		clips = new Environment();
        try{
            System.out.println("HELLO WORLD!");
            clips.load("src/clips/amazon2-facts.clp");
            clips.load("src/clips/amazon1-fules.clp");
            clips.reset();
            clips.run();
            System.out.println("archivos cargados");

            clips.eval("(facts)");

            List<FactAddressValue> techniqueFacts = clips.findAllFacts("product");
            int tNum = techniqueFacts.size();
            String messageStr="";
            String[] factsbk = new String[tNum];
            tNum=tNum+1000;
            for (int i = 1001; i <= tNum; i++)
            {   int j=0;
                FactAddressValue fv = clips.findFact("?f","product","(eq ?f:id " + i + ")");
                if (fv == null) continue;
                int id = ((NumberValue) fv.getSlotValue("id")).intValue();
                if(id==1001)
                {
                    int newStock = ((NumberValue) fv.getSlotValue("stock")).intValue()-1;
                    factsbk[j] = "(product (name "+((LexemeValue) fv.getSlotValue("name")).getValue()+")(id "+((NumberValue) fv.getSlotValue("id")).intValue()+") (marca "+((LexemeValue) fv.getSlotValue("marca")).getValue()+") (price "+((NumberValue) fv.getSlotValue("price")).intValue()+") (stock "+newStock+"))\n";
                }
                else
                {
                    factsbk[j] = "(product (name "+((LexemeValue) fv.getSlotValue("name")).getValue()+")(id "+((NumberValue) fv.getSlotValue("id")).intValue()+") (marca "+((LexemeValue) fv.getSlotValue("marca")).getValue()+") (price "+((NumberValue) fv.getSlotValue("price")).intValue()+") (stock "+((NumberValue) fv.getSlotValue("stock")).intValue()+"))\n";
                }
                j++;
            }

           // MultifieldValue backup = (MultifieldValue) clips.eval("(find-all-facts ((?f product)) TRUE)");
           /*String evaluar = "(find-all-facts ((?v product)) TRUE)";
            PrimitiveValue value = clips.eval(evaluar);
            System.out.println("facts guardados en multifield value");

            for (int i = 0; i < value.size(); i++) {
                //FactAddressValue fv = (FactAddressValue)((MultifieldValue) backup).get(i);
               // PrimitiveValue fv = backup.get(i);
                //String str = "";
                //str = fv.get(0).getFactSlot("name").toString();
                nombre = "";
                nombre = value.get(0).getFactSlot("name").toString();
                System.out.println("***"+nombre);
                if (backup.name==product) {
                    
                }
            }*/
            /*clips.build("(defrule remove  ?f <- (product (id 1003))=>(retract ?f))");
            System.out.println("rule delete create");
            clips.run();
            System.out.println("fact removidos");
            clips.eval("(facts)");
        
            File archivo = new File("src/clips/amazon2-facts.clp");
            if(archivo.delete())
            {
                System.out.println("archivo eliminado");
                clips.eval("(save src/clips/amazon2-facts.clp)");
            }
            else
            {
                System.out.println("No se pudo eliminar el archivo");
            }*/
           
        }
        catch(Exception e) {}
	}
}