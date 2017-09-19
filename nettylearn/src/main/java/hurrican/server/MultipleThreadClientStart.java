package hurrican.server;

import java.util.Scanner;

/**
 * Created by NewObject on 2017/8/25.
 */
public class MultipleThreadClientStart {
    public static void main(String[] args) {
        MultipleThreadClient client = new MultipleThreadClient();
        client.init(5);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String msg = scanner.nextLine();
            client.nextChannel().writeAndFlush(msg);
        }
    }
}
