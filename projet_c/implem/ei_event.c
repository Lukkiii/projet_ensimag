#include "ei_event.h"

static ei_widget_t widget_actif = NULL;

bool default_default_handle_func(ei_event_t*	event){
    return false;
    }

static ei_default_handle_func_t default_func = default_default_handle_func;

void ei_event_set_active_widget(ei_widget_t widget){
    widget_actif = widget;
}

ei_widget_t ei_event_get_active_widget(void){
    return widget_actif;
}

void ei_event_set_default_handle_func(ei_default_handle_func_t func){
    default_func = func;
}


ei_default_handle_func_t ei_event_get_default_handle_func(void){
    return default_func;
}
