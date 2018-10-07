package uk.co.akm.util.manager.password.clipboard

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable

/**
 * Created by Thanos Mavroidis on 26/09/2018.
 */
class ClipboardServiceImpl : ClipboardService, ClipboardOwner {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    override fun store(text: String) {
        clipboard.setContents(StringSelection(text), this)
    }

    override fun clear() {
        store("")
    }

    override fun lostOwnership(clipboard: Clipboard?, contents: Transferable?) {}
}