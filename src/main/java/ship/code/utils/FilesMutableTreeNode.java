package ship.code.utils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class FilesMutableTreeNode extends DefaultMutableTreeNode {
    public FilesMutableTreeNode(String name){
        super(name);
    }

    public TreeNode addNode(String[] path, int index, TreeNode newRoot) {
        if(index >= path.length){
            return this;
        }
        if(children != null && !this.children.isEmpty()) {
            Enumeration nodes = children();
            while (nodes.hasMoreElements()) {
                TreeNode n = (TreeNode) nodes.nextElement();
                if (index < path.length && n.toString().equals(path[index])) {
                    return ((FilesMutableTreeNode) n).addNode(path, ++index, newRoot);
                }
            }
        }
        FilesMutableTreeNode newNode = new FilesMutableTreeNode(path[index]);
        if(newRoot == null)
            newRoot = newNode;
        this.add(newNode);
        if (index < path.length)
            return newNode.addNode(path, ++index, newRoot);
        return newRoot;
    }

    public String getPathFile(){
        if(!this.isLeaf()){
            return "";
        }
        StringBuilder result = new StringBuilder();
        TreeNode[] treeNode = this.getPath();
        for(TreeNode n : treeNode){
            if(n.isLeaf())
                result.append(n.toString());
            else
                result.append(n.toString()).append("/");
        }
        return result.toString();
    }
}
