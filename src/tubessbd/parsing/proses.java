/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tubessbd.parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Immelman
 */
public class proses {
    File file = new File("C:\\Users\\Immelman\\Documents\\NetBeansProjects\\TubesSBD-Parsing\\src\\tubessbd\\parsing\\data.txt"); 
  
    BufferedReader br = new BufferedReader(new FileReader(file));
    private double z,x;
    private String[] tempselect;
    private String[] tempwhere = null;
    private String where;
    private String[] alias = new String[2];
    private String[] selectjoin=null;
    private String tempjoin;
    private String using;
    private String tempfrom;
    private String QEP;
    String[] QEPtampung = new String[2];
    private String selectt;
    int key=0;
    private boolean and = false;
    private boolean or = false;
    private double[] cost = new double[2];
    private String[] attjoin = new String[10];
    private String[] attfrom = new String[10];
    private String[] tampung;
    String Main = this.br.readLine();
    String[] MainSplit = Main.split(";");
    int B = Integer.parseInt(MainSplit[1].split(" ")[1]);
    int P = Integer.parseInt(MainSplit[0].split(" ")[1]);

    String Baris1 = this.br.readLine();
    String Baris2 = this.br.readLine();
    String Baris3 = this.br.readLine();
    String[] Baris1Split = Baris1.split(";");
    String[] Baris2Split = Baris2.split(";");
    String[] Baris3Split = Baris3.split(";");
    String[] Alltables = {Baris1Split[0], Baris2Split[0], Baris3Split[0]};
    int[] R = {Integer.parseInt(Baris1Split[2].split(" ")[1]), Integer.parseInt(Baris2Split[2].split(" ")[1]), Integer.parseInt(Baris3Split[2].split(" ")[1])};
    int[] n = {Integer.parseInt(Baris1Split[3].split(" ")[1]), Integer.parseInt(Baris2Split[3].split(" ")[1]), Integer.parseInt(Baris3Split[3].split(" ")[1])};
    int[] V = {Integer.parseInt(Baris1Split[4].split(" ")[1]), Integer.parseInt(Baris2Split[4].split(" ")[1]), Integer.parseInt(Baris3Split[4].split(" ")[1])};
    String[] column1 = Baris1Split[1].split(",");
    String[] column2 = Baris2Split[1].split(",");
    String[] column3 = Baris3Split[1].split(",");
    String[][] AllColumn = {column1, column2, column3};
    
    int[] BFR = new int[5];
    int[] fanout = new int[5];
    int[] TB = new int[5];
    int[] TBI = new int[5];
    
    public boolean Parserku(String n) {  //ubah boolean
        System.out.println(n);
        QEPtampung[1] = null;
        boolean keluar = false;
        tampung = n.split(" ");
        char cek = n.charAt(n.length()-1);
        this.cekJoin(n);
        this.cekFrom(n);
        where = this.cekWhere(n);
        selectt = this.cekSelect(n);
        this.cost();
        if (cek != ';'){
            System.out.println("Your Forgot ; at last query");
        } else {
            if (selectt != null){
                if (tempfrom != null){
                    if (tempjoin != null && using != null){
                        QEPtampung[0]= "PROJECTION "+selectt+" -- on the fly;       JOIN "+tempfrom+"."+using+"="+tempjoin+"."+using+" -- BNLJ;"+tempfrom+"       "+tempjoin+";Cost(worst case) = "+cost[0]+" block";
                        QEPtampung[1]= "PROJECTION "+selectt+" -- on the fly;       JOIN "+tempfrom+"."+using+"="+tempjoin+"."+using+" -- BNLJ;"+tempjoin+"       "+tempfrom+";Cost(worst case) = "+cost[1]+" block";
                        keluar = true;
                        
                    //join tidak ada    
                    } else if(tempjoin == null && using == null){
                        if (where != null){
                            if (selectt.equals("*")){
                                if(key==0){
                                    QEPtampung[0] = "SELECTION "+where+" -- A1 Non Key;"+tempfrom+";Cost = "+cost[0]+" block"; 
                                }else if(key==1){
                                    QEPtampung[0] = "SELECTION "+where+" -- A1 Key;"+tempfrom+";Cost = "+cost[0]+" block";
                                    QEPtampung[1] = "SELECTION "+where+" -- A2;"+tempfrom+";Cost = "+cost[1]+" block";
                                }else if(key>1){
                                    QEPtampung[0] = "SELECTION "+where+" -- A1 Key;"+tempfrom+";Cost = "+cost[0]+" block";
                                    QEPtampung[1] = "SELECTION "+where+" -- A2;"+tempfrom+";Cost = "+cost[1]+" block";
                                }
                                keluar = true; 
                            } else {
                                if (and){
                                    if(key==0){
                                        QEPtampung[0] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+" -- A1 Non Key;"+tempfrom+";Cost = "+cost[0]+" block"; 
                                    }else if(key==1){
                                        QEPtampung[0] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+" -- A1 Key;"+tempfrom+";Cost = "+cost[0]+" block";
                                        QEPtampung[1] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+" -- A2;"+tempfrom+";Cost = "+cost[1]+" block";
                                    }else if(key>1){
                                        QEPtampung[0] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+" -- A1 Key;"+tempfrom+";Cost = "+cost[0]+" block";
                                        QEPtampung[1] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+" -- A2;"+tempfrom+";Cost = "+cost[1]+" block";
                                    }
                                }else if (!and && !or){
                                    if(key==0){
                                        QEPtampung[0] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+" -- A1 Non Key;"+tempfrom+";Cost = "+cost[0]+" block"; 
                                    }else if(key==1){
                                        QEPtampung[0] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+" -- A1 Key;"+tempfrom+";Cost = "+cost[0]+" block";
                                        QEPtampung[1] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+" -- A2;"+tempfrom+";Cost = "+cost[1]+" block";
                                    }
                                } else if(or){ 
                                        QEPtampung[0] = "PROJECTION "+selectt+" -- on the fly;SELECTION "+where+";"+tempfrom+";Cost = "+cost[0]+" block";
                                }
                                keluar = true;
                            }
                        } else {
                            QEPtampung[0] = "PROJECTION "+selectt+" -- on the fly;"+tempfrom+";Cost = "+cost[0]+" block";
                            keluar = true;
                        }
                    } else if(tempjoin != null && using==null){
                            System.out.println("using salah");
                    }
                } else {
                    System.out.println("syntax error karena tidak ada from or typo");
                }
            } else {
                System.out.println("syntax error karena tidak ada select or typo");
            }
        }
        return keluar;
    }
    //start = 'select'
    //end = 'from'
    public String split(String __, String start, String end) {
		return __.substring(__.indexOf(start) + start.length() + 0x01, __.indexOf(end));
    }
    
    public String[] getAttribute(int index) {
        String[] select = null;
        switch (index) {
            case 0:
                select = column1;
                break;
            
            case 1:
                select = column2;
                break;
    
            case 2:
                select = column3;
                break;
        }
        
        return select;
    }
    
    public String cekSelect(String n){
        int indexTables;
        n = n.toLowerCase();
        int indexTables1=-1;
        int idx=0;
        List<String> tables = Arrays.asList(new String[] {"mahasiswa", "matakuliah", "registrasi"});
        
        boolean isJoin = false;
        isJoin = (n.indexOf("join") != -1);
        
        boolean isWhere = false;
        isWhere = (n.indexOf("where") != -1);
        
        //Select id, nama from mahasiswa where id = '2';
        String[] attribute = this.split(n, "select", "from").split(",");
        attribute[attribute.length-1] = attribute[attribute.length-1].substring(0, attribute[attribute.length-1].length()-1);

        String from;
        if (isJoin) {
            from = this.split(n, "from", "join");
            from = from.substring(0, from.length()-1);
            indexTables1 = tables.indexOf(tempjoin);
        } else from = this.split(n, "from", ";");
        
        System.out.println(from);
        
        if(n.indexOf(".")==-1){
            indexTables = tables.indexOf((isWhere) ? from.split(" ")[0] : from);
        }else{
            String[] from1 = from.split(" ");
            indexTables = tables.indexOf((isWhere) ? from.split(" ")[0] : from1[0]);
        }
        
        if (indexTables == -1) {
            System.out.println("[ERROR] Tables not found !!");
            System.exit(0);
        }
        
        // NON-JOIN
        String[] listAttribute = this.getAttribute(indexTables); //from
        System.out.println(listAttribute);
        z =0;
        if (attribute[0].indexOf('*') != -1 && !isJoin){
            if (isWhere) listAttribute[0] = this.split(n, "where", ";").split("=")[0];
            else attribute = listAttribute;
        } else if(attribute[0].indexOf('*') == -1 && !isJoin){
            for (int i=0; i < attribute.length; i++) {
                if (Arrays.asList(listAttribute).contains(attribute[i])) {
                    z++;
                }    
            }
            
            if(z!=attribute.length){
                return null;
            }
            
        //JOIN
        } else if (isJoin){
            String[] listAttribute1 = this.getAttribute(indexTables1); //join
            idx = listAttribute1.length;
            if (attribute[0].indexOf('*') != -1){
                String[] bintang = new String[attribute.length+listAttribute.length-1];
                attribute = listAttribute;
                attjoin = listAttribute1;
                attfrom = listAttribute;
                for (int j = 0; j < attribute.length; j++) {
                    bintang[j]=attribute[j];
                }
                int f=0;
                for (int i = attribute.length; i < bintang.length; i++) {
                    bintang[i]=listAttribute1[f];
                    f++;
                }
                return String.join(", ", bintang);
            }else{
                int i=0;
                int k=0;
                String[] cekatt= new String[2];
                while (i < attribute.length) {
                    cekatt = attribute[i].split("\\.");
                    if (cekatt.length>1) {
                        if(alias[0]!=null && alias[1]!=null){
                            if(cekatt[0].equals(alias[0])){ //cek di from
                                if (Arrays.asList(listAttribute).contains(cekatt[1])) {
                                    k++;
                                    int s = 0;
                                    while(attfrom[s]!=null){
                                        s++;
                                    }
                                    attfrom[s] = cekatt[1];
                                }
                            }else if(cekatt[0].equals(alias[1])){ //cek di join
                                if (Arrays.asList(listAttribute1).contains(cekatt[1])) {
                                    k++;
                                    int s = 0;
                                    while(attjoin[s]!=null){
                                        s++;
                                    }
                                    attjoin[s] = cekatt[1];
                                }
                            }

                        }else{
                            if(cekatt[0].equals(tempfrom)){ //cek di from
                                if (Arrays.asList(listAttribute).contains(cekatt[1])) {
                                    k++;
                                    int s = 0;
                                    while(attfrom[s]!=null){
                                        s++;
                                    }
                                    attfrom[s] = cekatt[1];
                                }
                            }else if(cekatt[0].equals(tempjoin)){ //cek di join
                                if (Arrays.asList(listAttribute1).contains(cekatt[1])) {
                                    k++;
                                    int s = 0;
                                    while(attjoin[s]!=null){
                                        s++;
                                    }
                                    attjoin[s] = cekatt[1];
                                }
                            }
                        }
                    }else{
                        if (Arrays.asList(listAttribute).contains(attribute[i])){
                            k++;
                            int s = 0;
                            while(attfrom[s]!=null){
                                s++;
                            }
                            attfrom[s] = attribute[i];
                        }
                        if (Arrays.asList(listAttribute1).contains(attribute[i])){
                            k++;
                            int s = 0;
                            while(attjoin[s]!=null){
                                s++;
                            }
                            attjoin[s]=attribute[i];
                        }
                        if(attribute[i].equals(using)){
                            k--;
                        }
                    }
                    i++;
                }
                
                if(k!=attribute.length){
                    System.out.println(k);
                    System.out.println(attribute.length);
                    for (int j = 0; j < attribute.length; j++) {
                        System.out.println(attribute[j]);
                    }
                    System.out.println("Select is empty");
                    System.exit(0);
                }
            }    
        }
        
        return attribute[0];
    }
    
    public String cekFrom(String n){
        boolean where=false;
        for(int i = 0; i<tampung.length;i++){
            if (tampung[i].equalsIgnoreCase("where")){
                where = true;
            }
        }
        
        String[] tempfromm = null;
        for(int i = 0; i<tampung.length;i++){
            if (tampung[i].equalsIgnoreCase("from")){
                if (tempjoin == null && where == true){
                    tempfrom = this.split(n, "from", "where");
                    tempfrom = tempfrom.substring(0,tempfrom.length()-1);
                } else if (tempjoin !=null){
                    tempfrom = this.split(n, "from", "join");
                    tempfrom = tempfrom.substring(0,tempfrom.length()-1);
                } else if (tempjoin==null && where == false){
                    tempfrom = this.split(n, "from", ";");
                } 
            }
        }
        tempfromm = tempfrom.split(" ");
        if (tempfromm.length == 2){
            alias[0] = tempfromm[1];
            tempfrom = tempfromm[0];
        }
        
        return tempfrom;
    }
    
    public String cekWhere(String n){
        
        int k=0;
        String[] listAttribute = null;
        n = n.toLowerCase();
        List<String> tables = Arrays.asList(new String[] {"mahasiswa", "matakuliah", "registrasi"});
        
        boolean isWhere;
        isWhere = (n.indexOf("where") != -1);
                
        if (!isWhere) return null;

        // check condition
        String condition = this.split(n, "where", ";");
        
        String[] attribute = new String[2];
        int lenAttr = 0;
        if (n.indexOf("and") != -1) {
            attribute[0] = condition.split("and")[0].split("=")[0];
            attribute[1] = condition.split("and")[1].split("=")[0];
            attribute[1] = attribute[1].substring(1, attribute[attribute.length-1].length());
            n = n.replace("and", "^");
            and = true;
            lenAttr = 2;
        } else if (n.indexOf("or") != -1) {
            attribute[0] = condition.split("or")[0].split("=")[0];
            attribute[1] = condition.split("or")[1].split("=")[0];
            attribute[1] = attribute[1].substring(1, attribute[attribute.length-1].length());
            n = n.replace("or", "v");
            or = true;
            lenAttr = 2;
        } else {
            attribute[0] = condition.split("=")[0];
            lenAttr = 1;
        }
        
        if (n.indexOf(".")==-1) {
            int indexTables = tables.indexOf(tempfrom);
            if (indexTables == -1) {
                System.out.println("[ERROR] Tables not found !!");
                System.exit(0);
            }
            listAttribute = this.getAttribute(indexTables);
        }
        
        
        z =0;
        for (int i = 0; i < lenAttr; i++) {
            if (attribute[i].equals(listAttribute[0])){
                key++;
            }
            if (Arrays.asList(listAttribute).contains(attribute[i])){
                z++;
            }  
        }
        if(z!=lenAttr){
            return null;
        } 
        
        if (attribute[1] != null){
            attribute[attribute.length-1] = attribute[attribute.length-1].substring(1, attribute[attribute.length-1].length());
        }
        
        n = n.split("where")[1];
        n = n.substring(1, n.length());
        n = n.substring(0, n.length()-1);
        
        return n;
    }
    
    public String cekJoin(String n){
        int cekusing=0;
        String[] candidateTables = new String[2];
        String[] candidateAttribute = new String[2];
        n = n.toLowerCase();       
        
        List<String> tables = Arrays.asList(new String[] {"mahasiswa", "matakuliah", "registrasi"});
        char cek = n.charAt(n.length()-1);
        
        if(cek != ';'){
            System.out.println("[ERR] YOUR FORGOT ;");
            System.exit(0);
        }
        boolean isJoin;
        isJoin = (n.indexOf("join") != -1);
        
        boolean isAlias;
        isAlias = (n.indexOf(".") != -1);
        
        // A. Split alias and tempJoin
        String tbTemp;
        String[] temp;
        
        if(isJoin){
            tbTemp = this.split(n, "from", "join");
            tbTemp = tbTemp.substring(0, tbTemp.length()-1);
            temp = tbTemp.split(" ");
            
            if (temp.length>1){
                alias[0] = temp[1];
                tempfrom = temp[0];
            }else{
                tempfrom = tbTemp;
            }
            
            if ((n.indexOf("using") == -1) && isJoin) {
                System.out.println("[ERR] Empty using !!");
                System.exit(0);
            }else{
                tbTemp = this.split(n, "join", "using");
                tbTemp = tbTemp.substring(0, tbTemp.length()-1);
                temp = tbTemp.split(" ");
                if(temp.length>1){
                    alias[1] = temp[1];
                    tempjoin = temp[0];
                } else {
                    tempjoin = tbTemp;
                }
            }
        }
        // END A.
        
        // Extra: check using
        if ((n.indexOf("using") == -1) && isJoin) {
            System.out.println("[ERR] Empty using !!");
            System.exit(0);
        }else if((n.indexOf("using") != -1) && isJoin) {
            String usingTemp = this.split(n, "using", ";");
            usingTemp = this.split(usingTemp, " ", ")");
            int indexTables = tables.indexOf(tempfrom);
            int indexTables1 = tables.indexOf(tempjoin);
            
            
            if(indexTables != -1 && indexTables1 != -1){
                String[] listAttribute = this.getAttribute(indexTables); //from
                String[] listAttribute1 = this.getAttribute(indexTables1); //join

                if (Arrays.asList(listAttribute).contains(usingTemp)){
                    cekusing++;
                }
                if (Arrays.asList(listAttribute1).contains(usingTemp)){
                    cekusing++;
                }
                
                if(cekusing!=2){
                    System.out.println("[ERR] Empty attribute using !!");
                    System.exit(0);
                }else{
                    using=usingTemp;
                } 
            }else if(indexTables != -1){
                System.out.println("[ERR] Empty from or typo!!");
                System.exit(0);
            }else if(indexTables1 != -1){
                System.out.println("[ERR] Empty join or typo!!");
                System.exit(0);
            }
            
        }
        String attrTemp = this.split(n, "select", "from");
        attrTemp = attrTemp.substring(0, attrTemp.length()-1);
        
        
        return tempjoin;
    }
    
    public double A1key (double b){
        return b/2;
    }
    
    public double A1nonkey (double b){
        return b;
    }
    
    public double A2(double b, int y){
        double log = Math.log(b)/Math.log(y);
        double h1 = Math.ceil(log);
        
        return h1+1;
    }
    
    public double A3(double b, int y){
        double log = Math.log(b)/Math.log(y);
        double h1 = Math.ceil(log);
        
        return h1+b;
        
    }
    
    public double BNLJ (double br, double bs){
        return (br*bs) + br;
    }
    
    public double cost(){
        cost[0] = -1;
        cost[1] = -1;
        double b = 0;
        int y1 = 0;
        double hasill =0;
        double bjoin = 0;
        List<String> tables = Arrays.asList(new String[] {"kendaraan", "transactions", "users"});
        int indexTables = tables.indexOf(tempfrom);
        int indexJoin = tables.indexOf(tempjoin);
        if(indexTables == 0){
            b = TB[0];
            y1 = fanout[0];
        } else if(indexTables == 1){
            b = TB[1];
            y1 = fanout[1];
        } else if(indexTables == 2){
            b = TB[2];
            y1 = fanout[2];
        }
        
        if(indexJoin == 0){
            bjoin = TB[0];
        } else if(indexJoin == 1){
            bjoin = TB[1];
        } else if(indexJoin == 2){
            bjoin = TB[2];
        }
        
        
        //untuk join
        if (tempjoin != null){
            cost[0] = this.BNLJ(b, bjoin); //kiriform
            cost[1] = this.BNLJ(bjoin, b); //kananform
        } else {
            if(where != null){
                if (and){ //ini untuk and atau tanpa and
                    if(key != 0){
                        if (key>1){
                            cost[0] = this.A1key(b);
                            cost[1] = this.A2(b, y1);
                        }else{
                            cost[0] = this.A1nonkey(b);
                            cost[1] = this.A2(b, y1);
                        }  
                    }else{
                        cost[0] = this.A1nonkey(b);
                    }
                }else if (or){ //ini untuk or dan tanya lagi apakah kalau or harus dibandingkan
                    if(key > 0){
                        if(key>1){
                            double a1 = this.A1key(b);
                            double a2 = this.A2(b, y1);
                            cost[0] = a1+a2;
                        }else{
                            double a1 = this.A1nonkey(b);
                            double a2 = this.A2(b, y1);
                            cost[0] = a1+a2;
                        }

                    }else {
                        cost[0] = this.A1nonkey(b);
                    }
                }else if (!and && !or){
                    if (key == 1){
                        cost[0] = this.A1key(b);
                        cost[1] = this.A2(b, y1);
                    }else {
                        cost[0] = this.A1nonkey(b);
                    }
                }
            }else{
                cost[0] = 0;
            }
        }
        return hasill;
    }
    
    public List<String> read(String locate) {
        File f = new File(locate);
        List<String> temp = new ArrayList<String>();

        BufferedReader reader;
        try {
                reader = new BufferedReader(new FileReader(locate));
                String line = reader.readLine();
                if (line != null) temp.add(line);
                
                while (line != null) {
                        line = reader.readLine();
                        temp.add(line);
                }
                reader.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return temp;
    }
    
    public void tosharepool(String datas) throws IOException {
        List<String> __ = this.read("C:\\Users\\Immelman\\Documents\\NetBeansProjects\\TubesSBD-Parsing\\src\\tubessbd\\parsing\\sharedpool.txt");
        if (!__.contains(datas)) {
            // TODO: Write files (append)
            BufferedWriter w = new BufferedWriter( new FileWriter("C:\\Users\\Immelman\\Documents\\NetBeansProjects\\TubesSBD-Parsing\\src\\tubessbd\\parsing\\sharedpool.txt", true) );
            if (__.size() != 0x00) w.newLine();
            w.write(datas);
            w.close();
        }
    }
    
    public proses(char s) throws FileNotFoundException, IOException{
        
        for (int i = 0; i < 3; i++) {
            BFR[i] = B/R[i];
            fanout[i] = B/(V[i] + P);
            TB[i] = (int) Math.ceil((double) n[i]/BFR[i]);
            TBI[i] = (int) Math.ceil((double) n[i]/fanout[i]);
        }
        
        if (s == '1') {
            for (int i = 0; i < 3; i++) {
                System.out.println("BFR "+Alltables[i]+" : "+BFR[i]);
                System.out.println("Fan Out Ratio "+Alltables[i]+" : "+fanout[i]);
            }
        }else if(s == '2'){
            for (int i = 0; i < 3; i++) {
                System.out.println("Tabel data "+Alltables[i]+" sebanyak : "+TB[i]);
                System.out.println("Indeks "+Alltables[i]+" : "+TBI[i]);
            }
        }else if(s == '3'){
            System.out.print("Cari record ke : ");
            Scanner sql = new Scanner(System.in);
            int record = sql.nextInt();
            System.out.print("Dari Table : ");
            Scanner tb = new Scanner(System.in);
            String namaTable = tb.nextLine().toLowerCase();
            
            double z;
            double x;
            for(int i = 0; i<3;i++){
                if (namaTable.equalsIgnoreCase(Alltables[i])){
                    z = Math.ceil((double)record/TBI[i]);
                    x = Math.ceil((double)record/TB[i]);
                    System.out.println(record);
                    System.out.println(TBI[i]);
                    System.out.println(TB[i]);
                    System.out.println("Menggunakan indeks, jumlah blok yang diakses = " + z + " blok");
                    System.out.println("Tanpa indeks, jumlah blok yang diakses = " + x + " blok");
                    break;
                }
            }
        }else if(s == '4'){
            String[] QEPshare;
            String[] QEPprint;
            System.out.print("Masukan Query = ");
            Scanner qry = new Scanner(System.in);
            String query = qry.nextLine();
            if (this.Parserku(query)){
                if (tempjoin == null){
                    System.out.println("========================================");
                        System.out.println("Tabel : "+tempfrom);
                    if (selectt.equals("*")){
                        List<String> tables = Arrays.asList(new String[] {"mahasiswa", "matakuliah", "registrasi"});
                        int indexTables = tables.indexOf(tempfrom);
                        String[] listAttribute = this.getAttribute(indexTables);
                        System.out.println("List Kolom : " + String.join(", ", listAttribute));
                    } else {
                        System.out.println("List Kolom : "+selectt);
                    }
                    System.out.println("========================================");
                    if (QEPtampung[1] != null){
                        System.out.println("QEP 1");
                        System.out.println(" ");
                        QEPprint = QEPtampung[0].split(";");
                        for (int i = 0; i < QEPprint.length; i++) {
                            System.out.println(QEPprint[i]);
                        }
                        System.out.println(" ");
                        System.out.println(" ");
                        System.out.println("QEP 2");
                        System.out.println(" ");
                        QEPprint = QEPtampung[1].split(";");
                        for (int i = 0; i < QEPprint.length; i++) {
                            System.out.println(QEPprint[i]);
                        }
                        
                        System.out.println("========================================");
                        if(cost[0]>cost[1]){
                            System.out.println("QEP Otimal = QEP2");
                            QEP = "Query = "+query.toLowerCase()+QEPtampung[1]+";";
                            this.tosharepool(QEP);
                        }else{
                            System.out.println("QEP Otimal = QEP 1");
                            QEP = "Query = "+query.toLowerCase()+QEPtampung[0]+";";
                            this.tosharepool(QEP);
                        }
                        
                    } else if (QEPtampung[1] == null){
                        System.out.println("QEP");
                        System.out.println(" ");
                        QEPprint = QEPtampung[0].split(";");
                        for (int i = 0; i < QEPprint.length; i++) {
                            System.out.println(QEPprint[i]);
                        }
                        System.out.println("QEP Otimal = QEP");
                        QEP = "Query = "+query.toLowerCase()+QEPtampung[0]+";";
                        this.tosharepool(QEP);
                    }
                    System.out.println("========================================"); 
                    
                } else {
                    String ranstemp;
                    System.out.println("========================================");
                    //kasih kalau select *
                    System.out.println("Tabel(1) : "+tempfrom);
                    if(query.indexOf('*') != -1){
                        ranstemp = String.join(",", attfrom);
                        System.out.printf("Atribute(1) : "+ranstemp);
                    }else{
                        System.out.printf("Atribute(1) : ");
                        for (int i = 0; i < 10; i++) {
                            if(attjoin[i]!=null){
                                System.out.printf(attjoin[i]);
                            }
                            if((i+1)<10 && attjoin[i+1]!=null){
                                System.out.printf(", ");
                            }
                        }
                    }
                    
                    System.out.println(" ");
                    System.out.println(" ");
                    
                    System.out.println("Tabel(2) : "+tempjoin);
                    if(query.indexOf('*') != -1){
                        ranstemp = String.join(",", attjoin);
                        System.out.printf("Atribute(2) : "+ranstemp);
                    }else{
                        System.out.printf("Atribute(2) : ");
                        for (int i = 0; i < 10; i++) {
                            if(attfrom[i]!=null){
                                System.out.printf(attfrom[i]);
                            }
                            if((i+1)<10 && attfrom[i+1]!=null){
                                System.out.printf(", ");
                            }
                        }
                    }
                    System.out.println(" ");
                    System.out.println("========================================");
                    System.out.println("QEP 1");
                        QEPprint = QEPtampung[0].split(";");
                        for (int i = 0; i < QEPprint.length; i++) {
                            System.out.println(QEPprint[i]);
                        }
                        
                    System.out.println(" ");
                    System.out.println(" ");
                    
                    System.out.println("QEP 2");
                        QEPprint = QEPtampung[1].split(";");
                        for (int i = 0; i < QEPprint.length; i++) {
                            System.out.println(QEPprint[i]);
                        }
                    
                    System.out.println("========================================");
                    if(cost[0]>cost[1]){
                        System.out.println("QEP Optimal = QEP2");
                        QEP = "Query = "+query.toLowerCase()+QEPtampung[1]+";";
                        this.tosharepool(QEP);
                    }else{
                        System.out.println("QEP Optimal = QEP1");
                        QEP = "Query = "+query.toLowerCase()+QEPtampung[0]+";";
                        this.tosharepool(QEP);
                    }
                    System.out.println("========================================");
                }
            }
        }else if(s == '5'){
            List<String> temp = this.read("C:\\Users\\Immelman\\Documents\\NetBeansProjects\\TubesSBD-Parsing\\src\\tubessbd\\parsing\\sharedpool.txt");
            
            for (int i=0; i < temp.size()-1; i++) {
                System.out.print(i+1+". ");
                System.out.println(temp.get(i).replace(";", "\n"));
            }
        }
        
    }
}
