public class MagitMain {

    public static void main(String[] args){
        UI uiConsole = new UI();
        try{
            uiConsole.runProgram();
        }
        catch(Exception e){
            System.out.println("had issues.....");
        }
    }
}
