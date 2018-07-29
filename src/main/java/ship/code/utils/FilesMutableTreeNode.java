package ship.code.utils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class FilesMutableTreeNode extends DefaultMutableTreeNode {
    public FilesMutableTreeNode(String name){
        super(name);
    }

    public void addNode(String[] path, int index){
        if(index >= path.length){
            return;
        }
        Enumeration nodes = children();
        while(nodes.hasMoreElements()){
            TreeNode n = (TreeNode) nodes.nextElement();
            if(index < path.length && n.toString().equals(path[index])){
                ((FilesMutableTreeNode) n).addNode(path, ++index);
                return;
            }
        }
        FilesMutableTreeNode newNode = new FilesMutableTreeNode(path[index]);
        this.add(newNode);
        if(index < path.length)
            newNode.addNode(path, ++index);
    }
}
