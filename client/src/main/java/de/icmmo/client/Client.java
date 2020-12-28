package de.icmmo.client;

import de.icmmo.shared.Packet;

import java.io.*;
import java.net.Socket;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {

    private Socket socket;
    private final Thread receiver;
    private Thread sender;
    private final Reader inputReader;
    protected final ConcurrentLinkedQueue<Packet> queue;


    public Client(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.receiver = new Receiver(socket, this);
        if (System.getProperties().getProperty("os.name").startsWith("Windows")){
            this.inputReader = new WindowsReader();
        } else {
            this.inputReader = new LinuxReader();
        }
        this.queue = new ConcurrentLinkedQueue<Packet>();
    }

    protected void runClient() {
        receiver.setDaemon(true);
        receiver.start();
        while (receiver.isAlive()){
            char c = inputReader.readNextChar();
            System.out.println(c == 'x');
            if (c == 'x'){
                break;
            }
        }
        inputReader.end();
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        //TODO: Remove this
        if (args.length == 0)
            args = new String[]{"localhost", "80"};
        System.out.println("Connecting...");
        if (args.length > 2){
            System.err.println("Invalid Args! Args should be length 2");
            return;
        }
        String ip = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (IllegalArgumentException e){
            System.err.println("Invalid Port!");
            return;
        }
        Client client;
        try{
            client = new Client(ip, port);
        } catch (IOException e) {
            System.err.println("Ungültige IP/Port!");
            return;
        }
        client.runClient();
    }
}
