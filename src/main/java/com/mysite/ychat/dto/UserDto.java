package com.mysite.ychat.dto;
import com.mysite.ychat.domain.User;

public class UserDto {
	private String id;
    private String username;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    // Getters, Setters (필요하면 추가)
    public String getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
}
