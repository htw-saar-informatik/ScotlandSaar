package com.denweisenseel.scotlandsaarexperimental.api;

/**
 * Created by denwe on 18.09.2017.
 */

public class RequestBuilder {


    private static String host = "http://10.0.2.2:8080/_ah/api/scotlandSaarAPI/v1/";

    public static final int CREATE_GAME = 0x00;
    private static final String CREATE_GAME_FUNCTION = "createGame";
    private static final int CREATE_GAME_ARGS = 3;

    public static final int JOIN_GAME = 0x01;
    private static final String JOIN_GAME_FUNCTION = "joinGame";
    private static final int JOIN_GAME_ARGS = 3;

    public static final int GET_GAMELIST = 0x02;
    private static final String GET_GAMELIST_FUNCTION = "gamelistbeancollection";
    private static final int GET_GAMELIST_ARGS = 0;

    public static final int CHAT_MESSAGE = 0x03;
    private static final String CHAT_MESSAGE_FUNCTION = "sendChatMessage";
    private static final int CHAT_MESSAGE_ARGS = 3;

    public static final int START_GAME = 0x04;
    private static final String START_GAME_FUNCTION = "startGame";
    private static final int START_GAME_ARGS = 2;

    public static String buildRequestUrl(int requestType, String[] args) {

        StringBuilder builder = new StringBuilder();
        builder.append(host);

        switch(requestType) {
            case CREATE_GAME:
                builder.append(CREATE_GAME_FUNCTION);
                for(int i = 0; i < CREATE_GAME_ARGS; ++i) {
                    builder.append("/");
                    builder.append(args[i]);
                }
                break;
            case GET_GAMELIST:
                builder.append(GET_GAMELIST_FUNCTION);
                for(int i = 0; i < GET_GAMELIST_ARGS; ++i) {
                    builder.append("/");
                    builder.append(args[i]);
                }
                break;
            case JOIN_GAME:
                builder.append(JOIN_GAME_FUNCTION);
                for(int i = 0; i < JOIN_GAME_ARGS; ++i) {
                    builder.append("/");
                    builder.append(args[i]);
                }
                break;
            case CHAT_MESSAGE:
                builder.append(CHAT_MESSAGE_FUNCTION);
                for(int i = 0; i < CHAT_MESSAGE_ARGS; ++i) {
                    builder.append("/");
                    builder.append(args[i]);
                }
                break;
            case START_GAME:
                builder.append(START_GAME_FUNCTION);
                for(int i = 0; i < START_GAME_ARGS; ++i) {
                    builder.append("/");
                    builder.append(args[i]);
                }
                break;

        }

        return builder.toString();
    }


}
