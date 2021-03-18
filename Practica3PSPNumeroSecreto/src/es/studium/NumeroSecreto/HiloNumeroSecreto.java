package es.studium.NumeroSecreto;

import java.io.DataInputStream;
import java.io.DataOutputStream; 
import java.io.IOException; 
import java.net.Socket;
public class HiloNumeroSecreto extends Thread {

	DataInputStream fentrada; 
	Socket socket; 
	boolean fin = false; 
	boolean partida = true;
	public HiloNumeroSecreto (Socket socket) { 
		this.socket = socket; 
		try { 
			fentrada = new DataInputStream(socket.getInputStream()); 
		}
		catch (IOException e) { 
			System.out.println("Error de E/S");
			e.printStackTrace(); 
		} 
	}  
	public static boolean esNumero(String cadena) {

		boolean esNumero;
		//Separamos el usuario del mensaje
		String[] mensaje = cadena.split("> ");
		String numero = mensaje[1];
		try {
			Integer.parseInt(numero);
			esNumero = true;
			
			
		} catch (NumberFormatException excepcion) {
			esNumero = false;
		}
		return esNumero;
	}
	public void run() throws ArrayIndexOutOfBoundsException 
	{ 
		ServidorNumeroSecreto.mensaje.setText("Número de conexiones actuales: " + ServidorNumeroSecreto.ACTUALES); 
		ServidorNumeroSecreto.mensaje3.setText("Número: " + ServidorNumeroSecreto.numeroSecreto);
		String texto = ServidorNumeroSecreto.textarea.getText(); 
		EnviarMensajes(texto); 
		while(!fin) { 
			String cadena = ""; 
			try { 
				cadena = fentrada.readUTF();
				String[] nombreNumeroPensado = cadena.split("> ");
				String nombre = nombreNumeroPensado[0];
				String numeroPensado = nombreNumeroPensado[1];
				if(cadena.trim().equals("*")) 
				{ 
					ServidorNumeroSecreto.ACTUALES--; 
					ServidorNumeroSecreto.CONEXIONES--;
					ServidorNumeroSecreto.mensaje.setText("Número de conexiones actuales: " + ServidorNumeroSecreto.ACTUALES); 
					fin=true;
				} 
				// El texto que el cliente escribe en el chat, 
				// se añade al textarea del servidor y se reenvía a todos los clientes 
				else {
					if (esNumero(cadena)==true)
					{
						if(Integer.parseInt(numeroPensado) < ServidorNumeroSecreto.numeroSecreto)
						{
							ServidorNumeroSecreto.textarea.append("\nSERVIDOR> " + nombre + " piensa que el número es el " + numeroPensado + ", pero el número es mayor. \n");
							texto = ServidorNumeroSecreto.textarea.getText();
							EnviarMensajes(texto);
							Thread.sleep(3000);
						}
						else if(Integer.parseInt(numeroPensado) > ServidorNumeroSecreto.numeroSecreto)
						{
							ServidorNumeroSecreto.textarea.append("\nSERVIDOR> " + nombre + " piensa que el número es el " + numeroPensado + ", pero el número es menor. \n");
							texto = ServidorNumeroSecreto.textarea.getText();
							EnviarMensajes(texto);
							Thread.sleep(3000);
						}
						else if(Integer.parseInt(numeroPensado) == ServidorNumeroSecreto.numeroSecreto)
						{
							ServidorNumeroSecreto.textarea.append("\nSERVIDOR> " + nombre + " piensa que el número es el " + numeroPensado + ", y ha acertado!\n FIN");	
							texto = ServidorNumeroSecreto.textarea.getText();
							EnviarMensajes(texto);
							partida=false;
						}

					}else {
						
							ServidorNumeroSecreto.textarea.append(cadena + "\n"); 
							texto = ServidorNumeroSecreto.textarea.getText(); 
							EnviarMensajes(texto);
						}
				} 
			}
			catch (Exception ex) 
			{ 
				fin=true; 
			}
		} 
	} 
	private void EnviarMensajes(String texto) 
	{ 
		for(int i=0;
				i<ServidorNumeroSecreto.CONEXIONES; i++) 
		{ 
			Socket socket = ServidorNumeroSecreto.tabla[i]; 
			try { DataOutputStream fsalida = new DataOutputStream(socket.getOutputStream());
			fsalida.writeUTF(texto); 
			} 
			catch (IOException e) { e.printStackTrace(); 
			} 
		} 
	} 

	
}
