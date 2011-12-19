package br.ufrj.cos.pinel.ligeiro.plugin.messages;

import org.eclipse.osgi.util.NLS;

/**
 * Get the right message for the interface.
 * 
 * (\w*)=.*
 * by
 * public static String \1;
 * 
 * @author Roque Pinel
 *
 */
public class Messages extends NLS
{
	/*
	 * (\w*)=.*
	 * by
	 * public static String \1;
	 */
	private static final String BUNDLE_NAME = "br.ufrj.cos.pinel.ligeiro.plugin.messages.messages"; //$NON-NLS-1$
	public static String LigeiroView_title;
	public static String LigeiroView_action_start_fpa_label;
	public static String LigeiroView_action_start_fpa_tip;
	public static String LigeiroView_action_load_files_label;
	public static String LigeiroView_action_load_files_tip;
	public static String LigeiroView_action_reset_fields_label;
	public static String LigeiroView_action_reset_fields_tip;
	public static String LigeiroView_files_section_title;
	public static String LigeiroView_files_properties_filename;
	public static String LigeiroView_files_properties_path;
	public static String LigeiroView_files_statistic_table_label;
	public static String LigeiroView_files_statistic_add_button_label;
	public static String LigeiroView_files_statistic_add_button_tip;
	public static String LigeiroView_files_statistic_add_dialog_title;
	public static String LigeiroView_files_statistic_remove_button_label;
	public static String LigeiroView_files_statistic_remove_button_tip;
	public static String LigeiroView_files_dependency_table_label;
	public static String LigeiroView_files_dependency_add_button_label;
	public static String LigeiroView_files_dependency_add_button_tip;
	public static String LigeiroView_files_dependency_add_dialog_title;
	public static String LigeiroView_files_dependency_remove_button_label;
	public static String LigeiroView_files_dependency_remove_button_tip;
	public static String LigeiroView_files_add_dialog_message;
	public static String LigeiroView_files_summary_table_label;
	public static String LigeiroView_files_summary_table_type;
	public static String LigeiroView_files_summary_table_total;
	public static String LigeiroView_files_summary_clear_tip;
	public static String LigeiroView_control_section_title;
	public static String LigeiroView_control_configuration_file_label;
	public static String LigeiroView_control_configuration_file_button_label;
	public static String LigeiroView_control_configuration_file_dialog_title;
	public static String LigeiroView_control_configuration_add_dialog_title;
	public static String LigeiroView_control_configuration_add_dialog_message;
	public static String LigeiroView_results_section_title;
	public static String LigeiroView_results_toolbar_clear_tip;
	public static String LigeiroView_results_table_namespace;
	public static String LigeiroView_results_table_element;
	public static String LigeiroView_results_table_data_function;
	public static String LigeiroView_results_table_transaction_function;
	public static String LigeiroView_results_table_type;
	public static String LigeiroView_results_table_ret_ftr;
	public static String LigeiroView_results_table_ret;
	public static String LigeiroView_results_table_ftr;
	public static String LigeiroView_results_table_det;
	public static String LigeiroView_results_table_complexity;
	public static String LigeiroView_results_table_complexity_value;
	public static String LigeiroView_results_data_function_total_label;
	public static String LigeiroView_results_data_function_total_tip;
	public static String LigeiroView_results_transaction_function_total_label;
	public static String LigeiroView_results_transaction_function_total_tip;
	public static String LigeiroView_results_unadjusted_fpa_total_label;
	public static String LigeiroView_results_unadjusted_fpa_total_tip;
	public static String LigeiroView_results_vaf_label;
	public static String LigeiroView_results_vaf_tip;
	public static String LigeiroView_results_adjusted_fpa_total_label;
	public static String LigeiroView_results_adjusted_fpa_total_tip;
	public static String LigeiroView_results_plus;
	public static String LigeiroView_results_times;
	public static String LigeiroView_results_equals;
	public static String LigeiroView_console_title;
	public static String LigeiroView_console_reading_configuration_file;
	public static String LigeiroView_console_reading_statistic_files;
	public static String LigeiroView_console_reading_dependency_files;
	public static String LigeiroView_console_file;
	public static String LigeiroView_console_read;
	public static String LigeiroView_console_element;
	public static String LigeiroView_console_element_plural;
	public static String LigeiroView_console_starting_fpa;
	public static String LigeiroView_console_done;
	public static String LigeiroView_xml_type_class;
	public static String LigeiroView_xml_type_class_plural;
	public static String LigeiroView_xml_type_dependency;
	public static String LigeiroView_xml_type_dependency_plural;
	public static String LigeiroView_xml_type_entity;
	public static String LigeiroView_xml_type_entity_plural;
	public static String LigeiroView_xml_type_service;
	public static String LigeiroView_xml_type_service_plural;
	public static String LigeiroView_xml_type_use_case;
	public static String LigeiroView_xml_type_use_case_plural;
	public static String LigeiroView_error_many_configuration_files;
	public static String LigeiroView_error_no_configuration_file;
	public static String LigeiroView_error_load_configuration_file;
	public static String LigeiroView_error_type_not_file;
	public static String LigeiroView_error_no_statistic_file;
	public static String LigeiroView_error_no_dependency_file;
	public static String LigeiroView_error_load_statistic_file;
	public static String LigeiroView_error_load_dependency_file;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
