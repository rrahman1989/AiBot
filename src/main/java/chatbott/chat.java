package chatbott;
import java.io.File;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;
public class chat {
	
	
	private static final boolean TRACE_MODE=false;
	
	public static void main(String args[])
	{
		try {
			String resourcepath=getpath();
			MagicBooleans.trace_mode=TRACE_MODE;
			Bot b=new Bot("super",resourcepath);
			Chat chatsession=new Chat(b);
			String textline="";
			
			
			while(true)
			{
				System.out.println("YOU : ");
				textline=IOUtils.readInputTextLine();
				
				if(textline==null || textline.length()<1)
				{
					textline=MagicStrings.null_input;
					
				}
				else if(textline.equals("q"))
				{
					System.exit(0);
				}
				else if(textline.equals("wq"))
				{
					b.writeQuit();
					System.exit(0);
				}
				else
				{
					String request=textline;
					String response=chatsession.multisentenceRespond(request);
                    if (response.contains("<sraix")) {
                    	System.out.println("We are inside <obb");
                        response = AIMLProcessor.respond(response, b.name, "user", chatsession);
                    }else if(response.contains("<oob")) {
                    	System.out.println("We are inside <obb");
                    }
					System.out.println("BOT :"+response);
				}
	
			}
		}
		catch (Exception e){
			
		}
	}
	private static String getpath()
	{
		File currd=new File(".");
		String path=currd.getAbsolutePath();
		String resourcepath=path + File.separator +"src" + File.separator +"main" +File.separator +"resources";
		return resourcepath;
	}
	
	
}