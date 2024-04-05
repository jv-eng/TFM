package com.jv.tfmprojectmobile.util.threads;

import com.jv.tfmprojectmobile.R;

import java.io.DataOutputStream;
import java.net.Socket;

public class EnviarFicheroThread implements Runnable {
    @Override
    public void run() {
        try {
            Socket sock = new Socket("192.168.1.51", 12345);
            byte [] pass = "mensaje".getBytes();
            int size = pass.length;
            DataOutputStream flujo_out = new DataOutputStream(sock.getOutputStream());
            flujo_out.writeInt(pass.length);
            flujo_out.write(pass);

        } catch (Exception e) {

        }
    }
}
