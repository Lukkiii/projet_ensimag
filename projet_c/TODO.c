// application -------------------------------------------------------------------------------------------------

void ei_app_create(ei_size_t main_window_size, bool fullscreen);
void ei_app_free(void);
void ei_app_run(void);
void ei_app_invalidate_rect(const ei_rect_t* rect);
void ei_app_quit_request(void);
ei_widget_t ei_app_root_widget(void);
ei_surface_t ei_app_root_surface(void);

// draw -------------------------------------------------------------------------------------------------

void	ei_draw_polyline	(ei_surface_t		surface,
				 ei_point_t*		point_array,
				 size_t			point_array_size,
				 ei_color_t		color,
				 const ei_rect_t*	clipper);

void	ei_draw_polygon		(ei_surface_t		surface,
				 ei_point_t*		point_array,
				 size_t			point_array_size,
				 ei_color_t		color,
				 const ei_rect_t*	clipper);

void	ei_draw_text		(ei_surface_t		surface,
				 const ei_point_t*	where,
				 ei_const_string_t	text,
				 ei_font_t		font,
				 ei_color_t		color,
				 const ei_rect_t*	clipper);

void	ei_fill			(ei_surface_t		surface,
				 const ei_color_t*	color,
				 const ei_rect_t*	clipper);

int	ei_copy_surface		(ei_surface_t		destination,
				 const ei_rect_t*	dst_rect,
				 ei_surface_t		source,
				 const ei_rect_t*	src_rect,
				 bool			alpha);

// event -------------------------------------------------------------------------------------------------

void				ei_event_set_active_widget(ei_widget_t widget);
ei_widget_t			ei_event_get_active_widget(void);
typedef bool			(*ei_default_handle_func_t)(ei_event_t* event);
void				ei_event_set_default_handle_func(ei_default_handle_func_t func);
ei_default_handle_func_t	ei_event_get_default_handle_func(void);

// placer -------------------------------------------------------------------------------------------------

void		ei_place	(ei_widget_t		widget,
				 ei_anchor_t*		anchor,
				 int*			x,
				 int*			y,
				 int*			width,
				 int*			height,
				 float*			rel_x,
				 float*			rel_y,
				 float*			rel_width,
				 float*			rel_height);

void		ei_placer_forget(ei_widget_t widget);

// types -------------------------------------------------------------------------------------------------

il y a bcp de structure deja faite

// utils -------------------------------------------------------------------------------------------------

simpa a regarde ce qu il y a

// widget_attributes -------------------------------------------------------------------------------------------------

ei_widgetclass_t*	ei_widget_get_class		(ei_widget_t		widget);
const ei_color_t*	ei_widget_get_pick_color	(ei_widget_t		widget);
ei_widget_t 		ei_widget_get_parent		(ei_widget_t		widget);
ei_widget_t 		ei_widget_get_first_child	(ei_widget_t		widget);
ei_widget_t 		ei_widget_get_last_child	(ei_widget_t		widget);
ei_widget_t 		ei_widget_get_next_sibling	(ei_widget_t		widget);
void*			ei_widget_get_user_data		(ei_widget_t		widget);
const ei_rect_t*	ei_widget_get_screen_location	(ei_widget_t		widget);
const ei_rect_t*	ei_widget_get_content_rect	(ei_widget_t		widget);
void	 		ei_widget_set_content_rect	(ei_widget_t		widget,
							 const ei_rect_t*	content_rect);

// widget_configure -------------------------------------------------------------------------------------------------

void			ei_frame_configure		(ei_widget_t		widget,
							 ei_size_t*		requested_size,
							 const ei_color_t*	color,
							 int*			border_width,
							 ei_relief_t*		relief,
							 ei_string_t*		text,
							 ei_font_t*		text_font,
							 ei_color_t*		text_color,
							 ei_anchor_t*		text_anchor,
							 ei_surface_t*		img,
							 ei_rect_ptr_t*		img_rect,
							 ei_anchor_t*		img_anchor);

static const int	k_default_button_border_width	= 4;	///< The default border width of button widgets.
static const int	k_default_button_corner_radius	= 10;	///< The default corner radius of button widgets.

void			ei_button_configure		(ei_widget_t		widget,
							 ei_size_t*		requested_size,
							 const ei_color_t*	color,
							 int*			border_width,
							 int*			corner_radius,
							 ei_relief_t*		relief,
							 ei_string_t*		text,
							 ei_font_t*		text_font,
							 ei_color_t*		text_color,
							 ei_anchor_t*		text_anchor,
							 ei_surface_t*		img,
							 ei_rect_ptr_t*		img_rect,
							 ei_anchor_t*		img_anchor,
							 ei_callback_t*		callback,
							 ei_user_param_t*	user_param);

void			ei_toplevel_configure		(ei_widget_t		widget,
							 ei_size_t*		requested_size,
							 ei_color_t*		color,
							 int*			border_width,
							 ei_string_t*		title,
							 bool*			closable,
							 ei_axis_set_t*		resizable,
						 	 ei_size_ptr_t*		min_size);


// widget -------------------------------------------------------------------------------------------------

typedef void		(*ei_widget_destructor_t)	(ei_widget_t widget);
typedef void		(*ei_callback_t)		(ei_widget_t		widget,
							 struct ei_event_t*	event,
							 ei_user_param_t	user_param);
ei_widget_t		ei_widget_create		(ei_const_string_t	class_name,
							 ei_widget_t		parent,
							 ei_user_param_t	user_data,
							 ei_widget_destructor_t destructor);

void			ei_widget_destroy		(ei_widget_t		widget);
bool	 		ei_widget_is_displayed		(ei_widget_t		widget);
ei_widget_t		ei_widget_pick			(ei_point_t*		where);

// widgetclass -------------------------------------------------------------------------------------------------

size_t		ei_widget_struct_size();
typedef char 	ei_widgetclass_name_t[20];
typedef ei_widget_t (*ei_widgetclass_allocfunc_t)	();
typedef void	(*ei_widgetclass_releasefunc_t)		(ei_widget_t	widget);
typedef void	(*ei_widgetclass_drawfunc_t)		(ei_widget_t		widget,
							 ei_surface_t		surface,
							 ei_surface_t		pick_surface,
							 ei_rect_t*		clipper);

typedef void	(*ei_widgetclass_setdefaultsfunc_t)	(ei_widget_t		widget);
typedef void	(*ei_widgetclass_geomnotifyfunc_t)	(ei_widget_t		widget);
typedef bool	(*ei_widgetclass_handlefunc_t)		(ei_widget_t		widget,
						 	 struct ei_event_t*	event);

typedef struct ei_widgetclass_t {
	ei_widgetclass_name_t			name;			///< The string name of this class of widget.
	ei_widgetclass_allocfunc_t		allocfunc;		///< The function that allocated instances of this class of widget.
	ei_widgetclass_releasefunc_t		releasefunc;		///< The function that releases all the resources used by an instance of this class of widget.
	ei_widgetclass_drawfunc_t		drawfunc;		///< The function that draws on screen an instance of this class of widget.
	ei_widgetclass_setdefaultsfunc_t	setdefaultsfunc;	///< The function that sets the default values to all the parameters of an instance of this class of widget.
	ei_widgetclass_geomnotifyfunc_t		geomnotifyfunc;		///< The function that is called to notify an instance of widget of this class that its geometry has changed.
	ei_widgetclass_handlefunc_t		handlefunc;		///< The function that is called when the application has received a user event referring to an instance of this class of widget.
	struct ei_widgetclass_t*		next;			///< A pointer to the next instance of ei_widget_class_t, allows widget class descriptions to be chained.
} ei_widgetclass_t;

void			ei_widgetclass_register		(ei_widgetclass_t* widgetclass);
ei_widgetclass_t*	ei_widgetclass_from_name	(ei_const_string_t name);




