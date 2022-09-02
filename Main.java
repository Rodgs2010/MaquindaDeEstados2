package maquina2;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static int co = 0;
    private static Path path;
    private static Scanner input;
    private static String[] s;
    private static int abertos = 0;
    private static int fechados = 0;
    private static int constantes = 0;
    private static int contproposicao = 0;
    private static int contStrings = 0;
    private static int contchar = 0;
    enum Estados {inicio, testaoperador,funario, fbinario};
    private static Estados estado;


    public static void abrirArquivo(){
        System.out.println("Coloque o nome do arquivo ou diretorio:");
        path = Paths.get("C:\\Users\\Rodrigo\\IdeaProjects\\PARA-CONCU-DISTRI\\src\\maquina2\\arqtexto");
        if(Files.exists(path)){
            lerArquivo();
        }else{
            System.out.printf("%s does not exist%n", path);
        }
    }
    public static void lerArquivo(){
        try{
            if(Files.isDirectory(path) || Files.isReadable(path)){
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
                for(Path p : directoryStream){
                    System.out.println("Lendo arquivo: " + p.getFileName());
                    input = new Scanner(Paths.get(String.valueOf(p)));
                    int contadortexto = 0;
                    while(input.hasNext()){
                        String pega = input.nextLine();
                        String[] texto = new String[100];
                        texto[contadortexto] = pega;

                        verificaFormula(texto[contadortexto]);
                        contStrings = 0;
                        constantes = 0;
                        abertos = 0;
                        fechados = 0;
                        contproposicao = 0;
                        co = 0;
                        contadortexto++;

                    }
                    System.out.println();
                }
            }
        }catch (NoSuchElementException | IllegalStateException elementException){
            System.err.println("File improperly formed. Terminating.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void fecharArquivo(){
        if(input != null){
            input.close();
            System.out.println("Fechando: " + path.getFileName());
        }
    }
    public static void verificaFormula(String text) {
        s = text.split(" ");
        estado = Estados.inicio;
        if (verificaParentesesFechado(s[0]) || verificaSimbolos(s[0])) {
            System.out.println(text + " = invalido");
            return;
        }
        for(int i = 0 ; i < s.length; i++){
            String letra = s[i];
            switch (estado) {
                case inicio -> {
                    if (verificaSimbolos(letra)) {
                        System.out.println(text + " = invalido");
                        return;
                    } else if (verificaProsicao(letra)) {
                        if(contproposicao > 2){
                            System.out.println(text + " = invalido");
                            return;
                        }
                        estado = Estados.inicio;
                    } else if (verifiConstante(letra)) {
                        estado = Estados.inicio;
                        constantes++;
                        if(constantes > 2 || co > 1){
                            System.out.println(text + " = invalido");
                            return;
                        }


                    } else if (verificaParentesesAberto(letra)) {
                        abertos++;
                        estado = Estados.testaoperador;
                    } else if(verificaParentesesFechado(letra)){
                        fechados++;
                        contproposicao = 0;
                        constantes = 0;
                        estado = Estados.inicio;
                    } else {

                        return;
                    }
                }
                case funario -> {
                    if (verificaSimbolos(letra) || verificaParentesesFechado(letra)) {
                        System.out.println(text + " = invalido");
                        return;
                    }else if(verificaParentesesAberto(letra)){
                        abertos++;
                        estado = Estados.testaoperador;
                    }else{

                        contproposicao++;
                        constantes++;
                        estado = Estados.inicio;
                    }
                }
                case testaoperador -> {
                    if (verificaOperadorUnario(letra)) {
                        estado = Estados.funario;
                    }else if(verificaOperadorBinario(letra)){
                        estado = Estados.fbinario;
                    }
                    else {
                        estado = Estados.inicio;
                    }
                }
                case fbinario -> {
                    if (verificaSimbolos(letra) || verificaParentesesFechado(letra)) {
                        System.out.println(text + " = invalido");
                        return;
                    }else if(verificaParentesesAberto(letra)){
                        abertos++;
                        estado = Estados.testaoperador;
                    } else{

                        contproposicao++;
                        constantes++;
                        estado = Estados.inicio;
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + estado);
            }
        }
        if(abertos != fechados){
            System.out.println(text + " = invalido");
        }else{
            System.out.println(text + " = valido");
        }

    }

    public static String proximoString(String[] s){
        return s[contStrings++];
    }
    public static char proximoChar(char[] s){
        return s[contchar++];
    }
    public static boolean verificaParentesesAberto(String texto){
        return (texto.equals("("));
    }
    public static boolean verificaParentesesFechado(String texto){
        return (texto.equals(")"));
    }
    public static boolean verificaOperadorBinario(String texto){
        return (texto.equals("∨") || texto.equals("∧") || texto.equals("→") || texto.equals("↔"));
    }
    public static boolean verificaOperadorUnario(String texto){
        return (texto.equals("¬"));
    }
    public static boolean verificaSimbolos(String texto){
        return (texto.equals("¬") || texto.equals("∨") || texto.equals("∧") || texto.equals("→") || texto.equals("↔"));
    }

    public static boolean verificaProsicao(String texto){
        char[] chartest = texto.toCharArray();
        for(int i = 0; i < chartest.length; i++){
            char t = proximoChar(chartest);
            if((t >= '0' && t <= '9') || (t >= 'a' && t <= 'z')){
                continue;
            }else{
                contchar = 0;
                return false;
            }
        }
        contproposicao++;
        contchar = 0;
        return true;
    }
    public static boolean verifiConstante(String texto){
        char[] chartes = texto.toCharArray();

        for(int i = 0; i < chartes.length; i++){
            char t = proximoChar(chartes);
            if(t == ('T')  || t == ('F')){
                co++;
            }
        }
        contchar = 0;
        return (texto.equals("T")  || texto.equals("F"));
    }

    public static void main(String[] args) {
        abrirArquivo();
        fecharArquivo();

    }
}
