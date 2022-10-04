package com.nilearning.palace.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.nilearning.palace.databinding.FragmentProfileBinding
import com.nilearning.palace.ProfileActivity
import com.nilearning.palace.R
import com.nilearning.palace.signin.SigninActivity
import com.nilearning.palace.util.PROFILE_IMAGE_URI
import com.nilearning.palace.util.PreferenceHelper
import com.nilearning.palace.util.USERNAME
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        if (PreferenceHelper.getToken() == "") {
            binding.profileCard.visibility = View.GONE
        } else {
            binding.profileCard.visibility = View.VISIBLE
            binding.signinWarning.visibility = View.GONE

            binding.profileImage.load(
                Uri.fromFile(File(PreferenceHelper.getString(PROFILE_IMAGE_URI, ""))))

            binding.userName.text = PreferenceHelper.getString(USERNAME, "")
        }

        binding.profileCard.setOnClickListener {
            val profileIntent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(profileIntent)
        }

        binding.loginActivity.setOnClickListener {
            val loginIntent = Intent(requireContext(), SigninActivity::class.java)
            startActivity(loginIntent)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}