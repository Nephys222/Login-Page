package com.nilearning.palace.signin

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nilearning.palace.util.PreferenceHelper
import com.nilearning.palace.databinding.FragmentSecondBinding
import com.nilearning.palace.ProfileActivity
import com.nilearning.palace.R
import com.nilearning.palace.util.MILLIS_IN_FUTURE
import com.nilearning.palace.util.PHONE_NUMBER
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val sharedViewModel: SigninViewModel by activityViewModels()
    private lateinit var countDown: CountDownTimer

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.entryCode.requestFocus()
        binding.passwordDescription.text = resources.getString(R.string.code_sent, PreferenceHelper.getString(
            PHONE_NUMBER, ""))

        binding.entryCode.addTextChangedListener {
            binding.wrongCode.visibility = View.INVISIBLE
        }

        binding.resume.setOnClickListener {
            if (checkValidity(binding.entryCode.text.toString())) {
                val session = sharedViewModel.signinResponse.value?.session
                sharedViewModel.checkCode(session!!, binding.entryCode.text.toString())
            } else {
                binding.wrongCode.visibility = View.VISIBLE
            }

            sharedViewModel.checkingStatus.observe(viewLifecycleOwner) {
                when (it) {
                    CheckingStatus.LOADING -> binding.loading.visibility = View.VISIBLE
                    CheckingStatus.ERROR -> {
                        binding.loading.visibility = View.GONE
                        Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT).show()
                    }
                    CheckingStatus.WRONG_CODE -> {
                        binding.loading.visibility = View.GONE
                        binding.wrongCode.visibility = View.VISIBLE
                    }
                    CheckingStatus.DONE -> {
                        binding.loading.visibility = View.GONE
                        PreferenceHelper.setToken(sharedViewModel.checkResponse.value?.token.toString())

                        val profileIntent = Intent(requireContext(), ProfileActivity::class.java)
                        profileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(profileIntent)
                    }
                    else -> {}
                }
            }

//            sharedViewModel.checkResponse.observe(viewLifecycleOwner) {
//                PreferenceHelper.setToken(it.token)
//            }
        }

        binding.retryButton.setOnClickListener {
            startTimer()
            sharedViewModel.register(PreferenceHelper.getString(PHONE_NUMBER, "").toLong())
        }
    }

    private fun startTimer() {
        countDown = object : CountDownTimer(MILLIS_IN_FUTURE, 1000) {
            override fun onTick(p0: Long) {
                val minutes = (p0 / 1000) / 60
                val seconds = (p0 / 1000) % 60

                if (_binding != null) binding.countdown.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.countdown.visibility = View.GONE
                binding.retryButton.visibility = View.VISIBLE
            }
        }.start()
        binding.countdown.visibility = View.VISIBLE
        binding.retryButton.visibility = View.INVISIBLE
    }

    private fun checkValidity(code: String): Boolean {
        if (code == "") {
            return false
        }
        else if (code.length != 5) {
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDown.cancel()
        _binding = null
    }
}