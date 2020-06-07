package nl.tudelft.cse.sem.shared;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class Endpoints {
    public static final String PING = "/ping";
    public static final String REGISTER = "/api/register";
    public static final String LOGIN = "/api/login";
    public static final String LEADERBOARD = "/api/score/leaderboard";
    public static final String SCORES = "/api/score/list";
    public static final String SUBMIT = "/api/score/submit";
}
