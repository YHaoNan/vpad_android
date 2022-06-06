package top.yudoge.vpad.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import top.yudoge.vpad.databinding.ItemServerListBinding
import top.yudoge.vpad.ui.ChooseVpadServerFragmentDirections
import top.yudoge.vpadapi.VPadServer


enum class ServerMode {
    Pad, Fader
}
class ServerListAdapter(
    private val onServerListChoosedListener: (ServerMode, VPadServer) -> Unit
) : RecyclerView.Adapter<ServerListAdapter.ViewHolder>() {
    private val serverList: MutableList<VPadServer> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemServerListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = serverList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(serverList[position])

    fun addVpadServer(vpadServer: VPadServer) {
        serverList.add(vpadServer)
        notifyItemInserted(serverList.size - 1)
    }

    fun clearVpadServer() {
        val size = serverList.size
        serverList.clear()
        notifyItemRangeRemoved(0, size)
    }

    inner class ViewHolder(
        private val binding: ItemServerListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(vpadServer: VPadServer) {
            binding.vpadServer = vpadServer
            binding.container.setOnClickListener {
                onServerListChoosedListener(ServerMode.Pad, vpadServer)
//                val action = ChooseVpadServerFragmentDirections
//                    .actionChooseVpadServerFragmentToPadFragment(vpadServer)
//                binding.root.findNavController().navigate(action)
            }
            binding.container.setOnLongClickListener {
                onServerListChoosedListener(ServerMode.Fader, vpadServer)
//                val action = ChooseVpadServerFragmentDirections
//                    .actionChooseVpadServerFragmentToFaderFragment(vpadServer)
//                binding.root.findNavController().navigate(action)
                true
            }
        }
    }

}