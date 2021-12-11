package TeamakerBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.login.LoginException;

import com.google.common.collect.Lists;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter{



    public static void main(String[] args)
            throws LoginException
    {
        JDA jda = JDABuilder.createDefault("OTE3NTI2NzM2MjY5MzUzMDQw.Ya5_XA.BpHVBOydjBO3Ce44YYG4oh72PU4").build();
        //You can also add event listeners to the already built JDA instance
        // Note that some events may not be received if the listener is added after calling build()
        // This includes events such as the ReadyEvent
        jda.addEventListener(new MessageListener());
        
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
    		
    		if (!isValidEvent(event)) {
    			return;
    		}
        	
        	MessageChannel channel = event.getChannel();
        	Guild g = event.getGuild();
        	        	        	
    		VoiceChannel emptyChannel = findEmptyChannel(event);
    		if (emptyChannel == null) {
    			channel.sendMessage("No empty channels avaliable").queue();
    			return;
    		}
	
    		VoiceChannel authorChannel = findAuthorChannel(event);
    		if (authorChannel == null) {
    			channel.sendMessage("Author is not found in voice channel").queue();
    			return;   		
    		}
    		
    		List<Member> members = makeTheRandomTeam(authorChannel)[1];    		
    		moveTeamToEmptyChannel(members, g, emptyChannel);
    }
    
    public boolean isValidEvent(MessageReceivedEvent event) {
		
    	if (event.isFromType(ChannelType.PRIVATE)){
			return false;
        }
    	
    	if	(!moveCommand(event)) {
			return false;
		}
    	        	
    	if (event.getAuthor().isBot()) {
    		return false;
    	}
    	        			
		if (!authorHasPermision(event)) {
			return false;
		}
				   	
    	return true;    	
    }
    
    
    public boolean moveCommand(MessageReceivedEvent event) {   	
    	return event.getMessage().getContentRaw().equalsIgnoreCase("ade ciganino");    	
    }
    
    
    public boolean authorHasPermision(MessageReceivedEvent event) {
    	Guild g = event.getGuild(); 
    	MessageChannel channel = event.getChannel();

    	Member authorMember = g.getMember(event.getAuthor());
    	if (authorMember == null) {
			channel.sendMessage("Author is not found").queue();
			return false;
		}
    	
    	for (Role r : authorMember.getRoles()) {
			if (r.getName().equalsIgnoreCase("BotAdmin")) {
				return true;
			}
		}
		
		return false;    	
    }
    
    
    public List[] makeTheRandomTeam(VoiceChannel authorChannel) {
		List<Member> members = new ArrayList<Member> (authorChannel.getMembers());		
		Collections.shuffle(members);
		return split(members);
	}
    
    
    public void moveTeamToEmptyChannel(List<Member> members, Guild g, VoiceChannel emptyChannel) {    	
    	for (Member m : members) {
    		g.moveVoiceMember(m, emptyChannel).queue();
    	}
    }
  
    
    public VoiceChannel findEmptyChannel(MessageReceivedEvent event) {   	
    	for (int i=0; i < event.getGuild().getChannels().size(); i++) {
    		if (event.getGuild().getChannels().get(i).getType().isAudio()) {
    			VoiceChannel channel = (VoiceChannel) event.getGuild().getChannels().get(i);
    			if (channel.getMembers().isEmpty()) {
    				return channel;
    			}
    		}
    	}
    	
		return null;    	     	
    }
    
    
    public VoiceChannel findAuthorChannel (MessageReceivedEvent event) {
    	User author = event.getAuthor();
    	for (GuildChannel c : event.getGuild().getChannels()) {
    		if (c.getType().isAudio()) {
    			VoiceChannel channel = (VoiceChannel) c;
    			for (Member m : channel.getMembers()){    				
    				if(m.getUser().getName().equalsIgnoreCase(author.getName())) {
    					return channel;
    				}
    			}
    		}
    	}
    	
		return null;
    }
    
    
    public static<T> List[] split(List<T> list) {
        // partition the list into two sublists
        List<List<T>> lists = Lists.partition(list, (list.size() + 1) / 2);     
        // return an array containing both lists
        return new List[] {lists.get(0), lists.get(1)};
    }

}