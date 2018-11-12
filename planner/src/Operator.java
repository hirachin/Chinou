import java.util.*;

class Operator {
	String name;
	Vector ifList;
	Vector addList;
	Vector deleteList;

	Operator(String theName, Vector theIfList, Vector theAddList, Vector theDeleteList) {
		name = theName;
		ifList = theIfList;
		addList = theAddList;
		deleteList = theDeleteList;
	}

	public Vector getAddList() {
		return addList;
	}

	public Vector getDeleteList() {
		return deleteList;
	}

	public Vector getIfList() {
		return ifList;
	}

	public String toString() {
		String result = "NAME: " + name + "\n" + "IF :" + ifList + "\n" + "ADD:" + addList + "\n" + "DELETE:"
				+ deleteList;
		return result;
	}

	public Vector applyState(Vector theState) {
		for (int i = 0; i < addList.size(); i++) {
			theState.addElement(addList.elementAt(i));
		}
		for (int i = 0; i < deleteList.size(); i++) {
			theState.removeElement(deleteList.elementAt(i));
		}
		return theState;
	}

	public Operator getRenamedOperator(int uniqueNum) {
		Vector vars = new Vector();
		// IfListの変数を集める
		for (int i = 0; i < ifList.size(); i++) {
			String anIf = (String) ifList.elementAt(i);
			vars = getVars(anIf, vars);
		}
		// addListの変数を集める
		for (int i = 0; i < addList.size(); i++) {
			String anAdd = (String) addList.elementAt(i);
			vars = getVars(anAdd, vars);
		}
		// deleteListの変数を集める
		for (int i = 0; i < deleteList.size(); i++) {
			String aDelete = (String) deleteList.elementAt(i);
			vars = getVars(aDelete, vars);
		}
		Hashtable renamedVarsTable = makeRenamedVarsTable(vars, uniqueNum);

		// 新しいIfListを作る
		Vector newIfList = new Vector();
		for (int i = 0; i < ifList.size(); i++) {
			String newAnIf = renameVars((String) ifList.elementAt(i), renamedVarsTable);
			newIfList.addElement(newAnIf);
		}
		// 新しいaddListを作る
		Vector newAddList = new Vector();
		for (int i = 0; i < addList.size(); i++) {
			String newAnAdd = renameVars((String) addList.elementAt(i), renamedVarsTable);
			newAddList.addElement(newAnAdd);
		}
		// 新しいdeleteListを作る
		Vector newDeleteList = new Vector();
		for (int i = 0; i < deleteList.size(); i++) {
			String newADelete = renameVars((String) deleteList.elementAt(i), renamedVarsTable);
			newDeleteList.addElement(newADelete);
		}
		// 新しいnameを作る
		String newName = renameVars(name, renamedVarsTable);

		return new Operator(newName, newIfList, newAddList, newDeleteList);
	}

	private Vector getVars(String thePattern, Vector vars) {
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				vars.addElement(tmp);
			}
		}
		return vars;
	}

	private Hashtable makeRenamedVarsTable(Vector vars, int uniqueNum) {
		Hashtable result = new Hashtable();
		for (int i = 0; i < vars.size(); i++) {
			String newVar = (String) vars.elementAt(i) + uniqueNum;
			result.put((String) vars.elementAt(i), newVar);
		}
		return result;
	}

	private String renameVars(String thePattern, Hashtable renamedVarsTable) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				result = result + " " + (String) renamedVarsTable.get(tmp);
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	public Operator instantiate(Hashtable theBinding) {
		// name を具体化
		String newName = instantiateString(name, theBinding);
		// ifList を具体化
		Vector newIfList = new Vector();
		for (int i = 0; i < ifList.size(); i++) {
			String newIf = instantiateString((String) ifList.elementAt(i), theBinding);
			newIfList.addElement(newIf);
		}
		// addList を具体化
		Vector newAddList = new Vector();
		for (int i = 0; i < addList.size(); i++) {
			String newAdd = instantiateString((String) addList.elementAt(i), theBinding);
			newAddList.addElement(newAdd);
		}
		// deleteListを具体化
		Vector newDeleteList = new Vector();
		for (int i = 0; i < deleteList.size(); i++) {
			String newDelete = instantiateString((String) deleteList.elementAt(i), theBinding);
			newDeleteList.addElement(newDelete);
		}
		return new Operator(newName, newIfList, newAddList, newDeleteList);
	}

	private String instantiateString(String thePattern, Hashtable theBinding) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				String newString = (String) theBinding.get(tmp);
				if (newString == null) {
					result = result + " " + tmp;
				} else {
					result = result + " " + newString;
				}
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	private boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}
}