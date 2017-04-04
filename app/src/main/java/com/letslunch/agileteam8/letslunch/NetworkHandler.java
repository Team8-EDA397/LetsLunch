package com.letslunch.agileteam8.letslunch;

import android.util.Log;


import java.io.DataInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.util.concurrent.ArrayBlockingQueue;



/**
 * Created by Carl-Henrik Hult on 2017-04-02.
 */

public class NetworkHandler
{
    MainActivity myActivity;
    ActiveThread thread;
    ArrayBlockingQueue data;
    private PassiveThread listenerThread;
    private boolean listenForConnections = true;



    private static final int PEER_PORT_NR = 50007;


    public NetworkHandler(MainActivity mainActivity)
    {
        this.myActivity = mainActivity;
    }

    /**
     * Starts the thread that listens for incoming connections from peer
     */
    public void startThread()
    {
        if(listenerThread == null || !listenerThread.isAlive())
        {
            listenForConnections = true;
            listenerThread = new PassiveThread();
            listenerThread.start();
        }
    }

    public void connectTo(String IP)
    {
        if(thread != null)
        {
            thread.stopThread();
        }
        thread = new ActiveThread(IP);
        data = new ArrayBlockingQueue<>(30);


        try
        {
            data.put((short) 5);
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        thread.start();

    }


    public void setNextData(short data) {
        try {
            this.data.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This thread can connect to a peer, exchange data, and send it to MainActivity
     */
    class ActiveThread extends Thread
    {
        String peerIp;
        Socket socket;

        /**
         * Constructor. Initializes thread
         *
         * @param ip The ip address to connect to
         */
        public ActiveThread(String ip)
        {
            peerIp = ip;
        }

        public Socket getSocket()
        {
            return socket;
        }

        /**
         * This runnable is the core of the thread. Preforms the actual network operations.
         */
        @Override
        @SuppressWarnings("unchecked")
        public void run()
        {
            try
            {
                //Set up socket and streams
                socket = new Socket(peerIp, PEER_PORT_NR);
                Log.i("myTag", "connected...");
                PrintStream oos = new PrintStream(socket.getOutputStream());

                //Send, and then receive data
                Log.i("myTag", "Transmitting");
                while(true)
                {
                    try
                    {
                        short temp = (short) data.take();

                        oos.print(2);
                        Log.i("myTag", "" + 2);

                    } catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        sleep(20);
                    } catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            } catch(IOException e)
            {

                Log.i("myTag", e.getMessage());
                e.printStackTrace();
            }
        }

        public void stopThread()
        {
            try
            {
                if(socket != null)
                {
                    socket.close();
                }
            } catch(IOException e)
            {
                e.printStackTrace();
            } finally
            {
                interrupt();
            }
        }


    }

    /**
     * This thread listens for a connection, exchanges data, and sends it to MainActivity
     */
    private class PassiveThread extends Thread
    {
        ServerSocket serverSocket = null;

        /**
         * This runnable is the core of the thread. Preforms the actual network operations.
         */
        @Override
        @SuppressWarnings("unchecked")
        public void run()
        {
            while(listenForConnections)
            {
                try
                {
                    //Set up socket
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(PEER_PORT_NR));

                    //Accept connection and set up streams
                    Socket client = serverSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());

                    //Receive, and then send data
                    Log.i("myTag", "Receiving and sending...");
                    String receivedData = (String)ois.readObject();

                    ois.close();

                    client.close();
                    serverSocket.close();

                    //Handle received data
                    final String data = receivedData;

                    myActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            myActivity.receiveDataFromPeer(data);
                        }
                    });
                }
                catch(IOException | ClassNotFoundException e)
                {
                    Log.i("myTag", e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        /**
         * Interrupts the thread and stops it from listening for connections
         */
        public void stopThread()
        {
            try
            {
                if (serverSocket != null)
                {
                    serverSocket.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                interrupt();
            }
        }
    }

}
