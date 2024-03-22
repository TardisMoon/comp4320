import java.io.*;
import java.net.*;

public class ClientTCP {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: prog ClientTCP servername PortNumber");
            System.exit(1);
        }

        String serverName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(serverName, portNumber);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                System.out.print("Enter OpCode (0-5): ");
                byte opCode = Byte.parseByte(userInput.readLine());

                System.out.print("Enter Operand1: ");
                int operand1 = Integer.parseInt(userInput.readLine());

                System.out.print("Enter Operand2: ");
                int operand2 = Integer.parseInt(userInput.readLine());

                System.out.print("Enter Request ID: ");
                short requestId = Short.parseShort(userInput.readLine());

                System.out.print("Enter Operation Name: ");
                String opName = userInput.readLine();

                long startTime = System.currentTimeMillis(); // Start measuring time

                // Send request to server
                output.writeInt(20); // Total Message Length for request
                output.writeByte(opCode); // Operation Code
                output.writeInt(operand1); // Operand 1
                output.writeInt(operand2); // Operand 2
                output.writeShort(requestId); // Request ID
                byte[] opNameBytes = opName.getBytes("UTF-16BE");
                output.writeShort(opNameBytes.length); // Operation Name Length
                output.write(opNameBytes);
                output.flush();

                // Receive response from server
                int tml = input.readInt(); // Total Message Length
                int result = input.readInt(); // Result
                byte errorCode = input.readByte(); // Error Code
                short responseId = input.readShort(); // Request ID

                long endTime = System.currentTimeMillis(); // Stop measuring time
                long roundTripTime = endTime - startTime; // Calculate round trip time

                System.out.println("Server Response:");
                System.out.println("Total Message Length: " + tml);
                System.out.println("Result: " + result);
                System.out.println("Error Code: " + errorCode);
                System.out.println("Request ID: " + responseId);
                System.out.println("Round Trip Time: " + roundTripTime + " ms");
            }
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }
}