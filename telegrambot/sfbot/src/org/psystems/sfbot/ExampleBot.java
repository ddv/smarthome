package org.psystems.sfbot;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class ExampleBot extends TelegramLongPollingBot {
	public static void main(String[] args) {
		ApiContextInitializer.init(); // Инициализируем апи
		TelegramBotsApi botapi = new TelegramBotsApi();
		try {
			botapi.registerBot(new ExampleBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotUsername() {
		return "DDV";
		// возвращаем юзера
	}

	
	@Override
	public void onUpdateReceived(Update e) {
	
		Message msg = e.getMessage();
		System.out.println("!! " + msg);
	
	
		
			// Check that the update contains a message and the message has a photo
		if (e.hasMessage() && e.getMessage().hasPhoto()) {
			// When receiving a photo, you usually get different sizes of it
			List<PhotoSize> photos = e.getMessage().getPhoto();

			// We fetch the bigger photo
			PhotoSize photo = photos.stream().sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
					.findFirst().orElse(null);

			System.out.println("!!! photo=" + photo);
			
			String path = getFilePath(photo);
			
			System.out.println("!!! photo path=" + path);
			
			try {
				File file = downloadPhotoByFilePath(path);
				FileOutputStream os = new FileOutputStream("./test/"+ path);

				byte[] buffer = new byte[1024];
				int bytesRead;

				FileInputStream is = new FileInputStream(file);
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				is.close();
				// flush OutputStream to write any buffered data to file
				os.flush();
				os.close();
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		
		
		if(msg.getText()==null) return;
		
		if (msg.getText().equals("/start")) {

			SendMessage sendMessage = new SendMessage(msg.getChatId(), "привет, " + msg.getFrom().getFirstName() +"!");
			
			 // Create ReplyKeyboardMarkup object
	        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
	        // Create the keyboard (list of keyboard rows)
	        List<KeyboardRow> keyboard = new ArrayList<>();
	        // Create a keyboard row
	        KeyboardRow row = new KeyboardRow();
	        // Set each button, you can also use KeyboardButton objects if you need something else than text
	        row.add("/foto");
	        row.add("/upload");
	        row.add("Row 1 Button 3");
	        // Add the first row to the keyboard
	        keyboard.add(row);
	        // Create another keyboard row
	        row = new KeyboardRow();
	        // Set each button for the second line
	        row.add("Row 2 Button 1");
	        row.add("Row 2 Button 2");
	        row.add("Row 2 Button 3");
	        // Add the second row to the keyboard
	        keyboard.add(row);
	        // Set the keyboard to the markup
	        keyboardMarkup.setKeyboard(keyboard);
	        // Add it to the message
	        
	        sendMessage.setReplyMarkup(keyboardMarkup);

			try {
				execute(sendMessage);
			} catch (TelegramApiException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

		}
		if (msg.getText().equals("/foto")) {
			 // Create send method
	        SendPhoto sendPhotoRequest = new SendPhoto();
	        // Set destination chat id
	        sendPhotoRequest.setChatId(msg.getChatId());
	        // Set the photo file as a new photo (You can also use InputStream with a method overload)
	        sendPhotoRequest.setNewPhoto(new File("./test/2018-01-29_001_164524.jpg"));
	        try {
	            // Execute the method
	            sendPhoto(sendPhotoRequest);
	        } catch (TelegramApiException ex) {
	            ex.printStackTrace();
	        }
		}
		if (msg.getText().equals("/upload")) {
			
		        
		    	
		}
		
	}

	@Override
	public String getBotToken() {
		return "";
		// Токен бота
	}
	
	public String getFilePath(PhotoSize photo) {
	    Objects.requireNonNull(photo);

	    if (photo.hasFilePath()) { // If the file_path is already present, we are done!
	        return photo.getFilePath();
	    } else { // If not, let find it
	        // We create a GetFile method and set the file_id from the photo
	        GetFile getFileMethod = new GetFile();
	        getFileMethod.setFileId(photo.getFileId());
	        try {
	            // We execute the method using AbsSender::execute method.
	        	org.telegram.telegrambots.api.objects.File file = execute(getFileMethod);
	            // We now have the file_path
	            return file.getFilePath();
	        } catch (TelegramApiException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    return null; // Just in case
	}
	
	public java.io.File downloadPhotoByFilePath(String filePath) {
	    try {
	        // Download the file calling AbsSender::downloadFile method
	        return downloadFile(filePath);
	    } catch (TelegramApiException e) {
	        e.printStackTrace();
	    }

	    return null;
	}

}