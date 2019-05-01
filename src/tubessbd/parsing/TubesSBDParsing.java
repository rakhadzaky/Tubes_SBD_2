/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tubessbd.parsing;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Immelman
 */
public class TubesSBDParsing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println(">> Menu Utama");
        System.out.println("1. Tampilkan BFR dan Fanout Ratio setiap tabel");
        System.out.println("2. Tampilkan Total Blok Data + Blok Index Setiap Tabel");
        System.out.println("3. Tampilkan Jumlah Blok yang Diakses untuk pencarian rekord");
        System.out.println("4. Tampilkan QEP dan Cost");
        System.out.println("5. Tampilkan isi File Shared Pool");
        System.out.println("");
        System.out.print(">> Masukan Pilihan anda = ");
        Scanner choice = new Scanner(System.in);
        char a = choice.next().charAt(0);
        
        if (((int)a) >= 49 && ((int)a) <= 53 ) {
            proses p = new proses(a);
        }else{
            System.out.println("Pilihan anda salah");
        }
        
    }
    
}
