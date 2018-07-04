package com.denweisenseel.scotlandsaarexperimental.api;

/**
 * Created by denwe on 18.09.2017.
 */

public class RequestBuilder {


    private static String host = "https://scotlandsaareu.appspot.com/_ah/api/scotlandSaarAPI/v1/";

    public static final int CREATE_GAME = 0x00;
    private static final String CREATE_GAME_FUNCTION = "createGame";

    public static final int JOIN_GAME = 0x01;
    private static final String JOIN_GAME_FUNCTION = "joinGame";

    public static final int GET_GAMELIST = 0x02;
    private static final String GET_GAMELIST_FUNCTION = "gamelistbeancollection";

    public static final int CHAT_MESSAGE = 0x03;
    private static final String CHAT_MESSAGE_FUNCTION = "sendChatMessage";

    public static final int START_GAME = 0x04;
    private static final String START_GAME_FUNCTION = "startGame";

    public static final int MAKE_MOVE = 0x05;
    private static final String MAKE_MOVE_FUNCTION = "makeMove";

    public static final int UPDATE_POSITION = 0x06;
    private static final String UPDATE_POSITION_FUNCTION = "submitPosition";

    public static String buildRequestUrl(int requestType){
        StringBuilder builder = new StringBuilder();
        builder.append(buildBasicRequestUrl(requestType));

        return builder.toString();
    }


    public static String buildRequestUrl(int requestType, String[] args) {

        StringBuilder builder = new StringBuilder();
        builder.append(buildBasicRequestUrl(requestType));
        builder.append(buildRequestUrlExtension(args));

        return builder.toString();
    }

    private static String buildBasicRequestUrl(int requestType) {

        StringBuilder builder = new StringBuilder();
        builder.append(host);

        switch (requestType) {
            case CREATE_GAME:
                builder.append(CREATE_GAME_FUNCTION);
                break;
            case GET_GAMELIST:
                builder.append(GET_GAMELIST_FUNCTION);
                break;
            case JOIN_GAME:
                builder.append(JOIN_GAME_FUNCTION);
                break;
            case CHAT_MESSAGE:
                builder.append(CHAT_MESSAGE_FUNCTION);
                break;
            case START_GAME:
                builder.append(START_GAME_FUNCTION);
                break;
            case MAKE_MOVE:
                builder.append(MAKE_MOVE_FUNCTION);
                break;
            case UPDATE_POSITION:
                builder.append(UPDATE_POSITION_FUNCTION);
                break;
        }

        return builder.toString();
    }

    private static String buildRequestUrlExtension(String[] args){

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < args.length; ++i) {
            builder.append("/");
            builder.append(args[i]);
        }

        return builder.toString();

    }

}
