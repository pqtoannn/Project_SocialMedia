package com.project.social_media.controllers.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipCheck {
    private boolean isFriend;
    private boolean isPending;
    private boolean isBlocked;
    private Long relationshipId; // null if not friend
}
