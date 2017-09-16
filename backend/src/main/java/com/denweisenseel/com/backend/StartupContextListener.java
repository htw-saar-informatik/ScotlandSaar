package com.denweisenseel.com.backend;

import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by denwe on 16.09.2017.
 */

public class StartupContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ObjectifyService.register(GameBoard.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
