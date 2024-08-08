#include "ei_widgetclass.h"
#include <string.h>

static ei_widgetclass_t* class_list = NULL;

/**
 * @brief	Registers a class to the program so that widgets of this class can be created
 * 		with \ref ei_widget_create.
 *		This must be done only once per widget class in the application.
 *
 * @param	widgetclass	The structure describing the class.
 */
void			ei_widgetclass_register		(ei_widgetclass_t* widgetclass){
    widgetclass->next = class_list;
    class_list = widgetclass;
}



/**
 * @brief	Returns the structure describing a class, from its name.
 *
 * @param	name		The name of the class of widget.
 *
 * @return			The structure describing the class.
 */
ei_widgetclass_t*	ei_widgetclass_from_name	(ei_const_string_t name){
    ei_widgetclass_t* current = class_list;
    while (strcmp(current->name, name) != 0)
    {
        current = current->next;
    }
    return current;
}


/**
 * @brief free all chained class instance
*/
void ei_widgetclass_free_register (){
    ei_widgetclass_t* current = class_list;
    while (current != NULL)
    {
        class_list = class_list->next;
        free(current);
        current = class_list;
    }
}