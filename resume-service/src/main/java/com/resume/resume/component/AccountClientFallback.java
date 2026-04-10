package com.resume.resume.component;

import com.resume.resume.client.AccountClient;

public class AccountClientFallback implements AccountClient {
    @Override
    public Boolean verifyUser(String emailId) {
        return null;
    }
}
