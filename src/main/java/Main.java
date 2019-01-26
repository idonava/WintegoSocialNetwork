import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length<2){
            System.out.println("argument 1: username\n" +
                    "argument 2: password\n" +
                    "argument 3  - print full logs (optional): any string");
        }
        else if (args.length==2){
            Application application = new Application(args[0], args[1],false);

        }
        else {
            Application application = new Application(args[0], args[1], true);
        }
    }
}
