import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(private val context: Context, private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val layoutParams = view.layoutParams as RecyclerView.LayoutParams
        val position = layoutParams.viewAdapterPosition

        if (position == RecyclerView.NO_POSITION) {
            return
        }

        // Add margins (convert pixels to dp)
        val marginDp = margin.toDp(view.context)

        outRect.top = marginDp
        outRect.bottom = marginDp
        outRect.left = marginDp
        outRect.right = marginDp
    }

    private fun Int.toDp(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
        ).toInt()
    }
}
