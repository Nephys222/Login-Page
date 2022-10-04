package com.nilearning.palace.signin

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nilearning.palace.PrivacyActivity
import com.nilearning.palace.util.PHONE_NUMBER
import com.nilearning.palace.util.PreferenceHelper
import com.nilearning.palace.R
import com.nilearning.palace.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SigninViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.entryNumber.requestFocus()
        val privacyText = SpannableString(resources.getString(R.string.privacy_text))

        val privacyLink = object: ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(requireContext(), PrivacyActivity::class.java)
                startActivity(intent)
            }
        }

        privacyText.setSpan(privacyLink, 0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.checkText.text = privacyText
        binding.checkText.movementMethod = LinkMovementMethod.getInstance()


//        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
//            binding.resume.isEnabled = isChecked == true
//        }

        binding.entryNumber.addTextChangedListener {
            binding.wrongPhoneNumber.visibility = View.INVISIBLE
        }

        binding.resume.setOnClickListener {
            if (!binding.checkBox.isChecked) {
                Snackbar.make(binding.root, R.string.privacy_warning, Snackbar.LENGTH_SHORT).show()
            } else if (checkPhoneNumber(binding.entryNumber.text.toString())) {

                val phoneNumber = "993${binding.entryNumber.text.toString()}".toLong()
                sharedViewModel.register(phoneNumber)

                sharedViewModel.signingStatus.observe(viewLifecycleOwner) {
                    when (it) {
                        SigningStatus.LOADING -> binding.loading.visibility = View.VISIBLE
                        SigningStatus.ERROR -> {
                            binding.loading.visibility = View.GONE
                            Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT).show()
                        }
                        SigningStatus.WRONG_NUMBER -> {
                            binding.loading.visibility = View.GONE
                            Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT).show()
                        }
                        SigningStatus.DONE -> {
                            binding.loading.visibility = View.GONE
                            PreferenceHelper.putString(PHONE_NUMBER, phoneNumber.toString())
                            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                        }
                        else -> {}
                    }
                }


            } else {
                binding.wrongPhoneNumber.visibility = View.VISIBLE
            }
        }
    }

    private fun checkPhoneNumber(phoneNumber: String): Boolean {
        if (phoneNumber == "") {
            return false
        }
        else if (!phoneNumber.startsWith("6")) {
            return false
        }
        else if (phoneNumber.length != 8) {
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}