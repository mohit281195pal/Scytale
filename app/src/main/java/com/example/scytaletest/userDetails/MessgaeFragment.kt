package com.example.Scytale.userDetails

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.Scytale.database.MessageRepository

import com.example.scytaletest.R
import com.example.scytaletest.database.MessageDatabase
import com.example.scytaletest.databinding.UserDetailsFragmentBinding
import com.example.scytaletest.userDetails.MessageViewModel
import com.example.scytaletest.userDetails.MyRecycleViewAdapter

class MessgaeFragment : Fragment() {

    private lateinit var messageViewModel: MessageViewModel
    private lateinit var binding: UserDetailsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.user_details_fragment, container, false
        )

        val application = requireNotNull(this.activity).application

        val dao = MessageDatabase.getInstance(application).messageDatabaseDao

        val repository = MessageRepository(dao)

        val factory = MessageFactory(repository, application)

        messageViewModel =
            ViewModelProvider(this, factory).get(MessageViewModel::class.java)
        binding.userDelailsLayout = messageViewModel

        binding.lifecycleOwner = this

        messageViewModel.errotoast.observe(viewLifecycleOwner, Observer { hasError ->
            if (hasError == true) {
                Toast.makeText(requireContext(), "Please type a message", Toast.LENGTH_SHORT).show()
                messageViewModel.donetoast()
            }
        })

        messageViewModel.messageSent.observe(viewLifecycleOwner, Observer { message ->
            if (message == null) {
                messageViewModel.donemessage()
            }
        })


        messageViewModel.navigateto.observe(viewLifecycleOwner, Observer { details ->
            if (!details.equals("")) {
                val bundle = Bundle()
                if (details.equals("back")) {
                    bundle.putString("details", "")
                }
                else if (details.equals("details")) {
                    bundle.putString("details", "details")
                }
                NavHostFragment.findNavController(this).navigate(R.id.action_message_to_signup,bundle)
                messageViewModel.doneNavigating()
            }
        })

        initRecyclerView()

        return binding.root

    }


    private fun initRecyclerView() {
        binding.messageRecyclerView.layoutManager = LinearLayoutManager(this.context)
        displayUsersList()
    }


    private fun displayUsersList() {
        Log.i("MYTAG", "Inside ...UserDetails..Fragment")
        messageViewModel.message.observe(viewLifecycleOwner, Observer {
            PreferenceManager.getDefaultSharedPreferences(context).getString("email", "")
            if (it.size > 0)
                binding.messageRecyclerView.adapter = MyRecycleViewAdapter(
                    messageViewModel,
                    it,
                    PreferenceManager.getDefaultSharedPreferences(context).getString("email", "")
                )
        })

    }


}