package alast.hm.Prevalent;

import alast.hm.Model.Users;

public class Prevalent {

    private static Users currentOnlineUser;

    public static Users getCurrentOnlineUser() {
        return currentOnlineUser;
    }

    public static void setCurrentOnlineUser(Users currentOnlineUser) {
        Prevalent.currentOnlineUser = currentOnlineUser;
    }
}
