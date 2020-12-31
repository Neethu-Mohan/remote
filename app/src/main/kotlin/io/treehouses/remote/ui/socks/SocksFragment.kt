package io.treehouses.remote.ui.socks

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Message
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.treehouses.remote.Constants
import io.treehouses.remote.R
import io.treehouses.remote.bases.BaseFragment
import io.treehouses.remote.databinding.ActivitySocksFragmentBinding
import io.treehouses.remote.databinding.DialogAddProfileBinding
import io.treehouses.remote.ui.status.StatusViewModel

class SocksFragment : BaseFragment(){
    protected val viewModel: SocksViewModel by viewModels(ownerProducer = { this })
    private var startButton: Button? = null
    private var addProfileButton: Button? = null
    private var addingProfileButton: Button? = null
    private var cancelProfileButton: Button? = null
    private var textStatus: TextView? = null
    private var adapter: ArrayAdapter<String>? = null
    private lateinit var dialog: Dialog
    private lateinit var password: EditText
    private lateinit var serverPort: EditText
    private lateinit var localPort: EditText
    private lateinit var localAddress: EditText
    private lateinit var serverHost: EditText
    private var profileName: java.util.ArrayList<String>? = null
    private var portList: ListView? = null
    var bind: ActivitySocksFragmentBinding? = null
    var bindProfile: DialogAddProfileBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = ActivitySocksFragmentBinding.inflate(inflater, container, false)
        bindProfile = DialogAddProfileBinding.inflate(inflater, container, false)
        viewModel.onLoad()
        profileName = ArrayList()
        addProfileButton = bind!!.btnAddProfile
        portList = bind!!.profiles
        initializeDialog()
        addProfileButtonListeners(dialog)
        portList = bind!!.profiles
        return bind!!.root
    }

    private fun addPortListListener() {
        portList!!.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.CustomAlertDialogStyle))
            val selectedString = profileName!![position]
            builder.setTitle("Delete Profile $selectedString ?")
            builder.setPositiveButton("Confirm") { dialog, _ ->
                listener.sendMessage("treehouses shadowsocks remove $selectedString ")
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }
    }

    private fun initializeDialog(){
        dialog = Dialog(requireContext())
        addPortListListener()

        dialog.setContentView(bindProfile!!.root)

        serverHost = bindProfile!!.ServerHost

        localAddress = bindProfile!!.LocalAddress
        localPort = bindProfile!!.localPort
        serverPort = bindProfile!!.serverPort
        password = bindProfile!!.password
        addingProfileButton = bindProfile!!.addingProfileButton
        cancelProfileButton = bindProfile!!.cancel
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        window.setBackgroundDrawableResource(android.R.color.transparent)

        initializeObservers()
    }

    private fun initializeObservers()
    {
        viewModel.serverHostText.observe(viewLifecycleOwner, Observer {
            serverHost.setText(it)
        });

        viewModel.localPortText.observe(viewLifecycleOwner, Observer {
            localPort.setText(it)
        });

        viewModel.localAddressText.observe(viewLifecycleOwner, Observer {
            localAddress.setText(it)
        });

        viewModel.passwordText.observe(viewLifecycleOwner, Observer{
            password.setText(it)
        })

        viewModel.addProfileButtonText.observe(viewLifecycleOwner, Observer {
            addingProfileButton?.setText(it)
        })
    }


    private fun addProfileButtonListeners(dialog: Dialog) {

        addProfileButton!!.setOnClickListener {
            dialog.show()
        }
        cancelProfileButton!!.setOnClickListener {
            dialog.dismiss()
        }
        addingProfileButton!!.setOnClickListener {
            if (serverHost.text.toString().isNotEmpty() && localAddress.text.toString().isNotEmpty() && localPort.text.toString().isNotEmpty() && serverPort.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                val ServerHost = serverHost.text.toString()
                val LocalAddress = localAddress.text.toString()
                val LocalPort = localPort.text.toString()
                val ServerPort = serverPort.text.toString()
                val Password = password.text.toString()

                val message = "treehouses shadowsocks add { \\\"server\\\": \\\"$ServerHost\\\", \\\"local_address\\\": \\\"$LocalAddress\\\", \\\"local_port\\\": $LocalPort, \\\"server_port\\\": $ServerPort, \\\"password\\\": \\\"$Password\\\", \\\"method\\\": \\\"rc4-md5\\\" }"
                listener.sendMessage(message)
                addProfileButton?.text = "Adding......"
                addProfileButton?.isEnabled = false
                dialog.dismiss()
            }
            else{
                Toast.makeText(requireContext(), "Missing Information", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setUserVisibleHint(visible: Boolean) {
        if (visible) {
            if (isListenerInitialized()) {
                mChatService = listener.getChatService()
                mChatService!!.updateHandler(mHandler)
                profileName = ArrayList()
                listener.sendMessage("treehouses shadowsocks list")
            }

        }
    }

    override fun getMessage(msg: Message) {
        if (msg.what == Constants.MESSAGE_READ) {
            val readMessage: String = msg.obj as String
            viewModel.onRead(readMessage)
        }

    }

}