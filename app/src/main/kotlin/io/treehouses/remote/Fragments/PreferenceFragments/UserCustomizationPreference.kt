package io.treehouses.remote.Fragments.PreferenceFragments

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import io.treehouses.remote.R
import io.treehouses.remote.bases.BasePreferenceFragment
import io.treehouses.remote.utils.KeyUtils
import io.treehouses.remote.utils.SaveUtils
import io.treehouses.remote.utils.SettingsUtils

class UserCustomizationPreference: BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.user_customization_preferences, rootKey)
        val clearCommandsList = findPreference<Preference>("clear_commands")
        val resetCommandsList = findPreference<Preference>("reset_commands")
        val clearNetworkProfiles = findPreference<Preference>("network_profiles")
        val clearSSHHosts = findPreference<Preference>("ssh_hosts")
        val clearSSHKeys = findPreference<Preference>("ssh_keys")
        SettingsUtils.setClickListener(this, clearCommandsList)
        SettingsUtils.setClickListener(this, resetCommandsList)
        SettingsUtils.setClickListener(this, clearNetworkProfiles)
        SettingsUtils.setClickListener(this, clearSSHHosts)
        SettingsUtils.setClickListener(this, clearSSHKeys)
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            "clear_commands" -> clearCommands()
            "reset_commands" -> resetCommands()
            "network_profiles" -> networkProfiles()
            "ssh_hosts" -> clearSSHHosts()
            "ssh_keys" -> clearSSHKeys()
        }
        return false
    }

    private fun clearCommands() {
        createAlertDialog("Clear Commands List", "Would you like to completely clear the commands list that is found in terminal? ", "Clear", CLEAR_COMMANDS_ID)
    }

    private fun resetCommands() {
        createAlertDialog("Default Commands List", "Would you like to reset the command list to the default commands? ", "Reset", RESET_COMMANDS_ID)
    }

    private fun networkProfiles() {
        createAlertDialog("Clear Network Profiles", "Would you like to remove all network profiles? ", "Clear", NETWORK_PROFILES_ID)
    }

    private fun clearSSHHosts() {
        createAlertDialog("Clear All SSH Hosts", "Would you like to delete all SSH Hosts? ", "Clear", CLEAR_SSH_HOSTS)
    }

    private fun clearSSHKeys() = createAlertDialog("Clear All SSH Keys", "Would you like to delete all SSH Keys?", "Clear", CLEAR_SSH_KEYS)

    private fun clearNetworkProfiles() {
        clear("profiles", "Network Profiles have been reset")
    }

    private fun clear(subject: String, message: String) {
        when (subject) {
            "profiles" -> {
                SaveUtils.clearProfiles(requireContext())
            }
            "commandsList" -> {
                SaveUtils.clearCommandsList(requireContext())
            }
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onClickDialog(id: Int) {
        when (id) {
            CLEAR_COMMANDS_ID -> {
                clear("commandsList", "Commands List has been Cleared")
            }
            RESET_COMMANDS_ID -> {
                SaveUtils.clearCommandsList(requireContext())
                SaveUtils.initCommandsList(requireContext())
                Toast.makeText(context, "Commands has been reset to default", Toast.LENGTH_LONG).show()
            }
            NETWORK_PROFILES_ID -> clearNetworkProfiles()
            CLEAR_SSH_HOSTS -> SaveUtils.deleteAllHosts(requireContext())
            CLEAR_SSH_KEYS -> KeyUtils.deleteAllKeys(requireContext())
        }
    }

    companion object {
        private const val CLEAR_COMMANDS_ID = 1
        private const val RESET_COMMANDS_ID = 2
        private const val NETWORK_PROFILES_ID = 3
        private const val CLEAR_SSH_HOSTS = 4
        private const val CLEAR_SSH_KEYS = 5
    }
}