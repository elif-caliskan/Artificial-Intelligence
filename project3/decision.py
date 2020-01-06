import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
from sklearn.datasets import load_iris
from queue import Queue
from queue import LifoQueue

def main():
    iris_data = load_iris().data
    iris_target = load_iris().target.reshape(150,1)

    iris_data = np.hstack((iris_data,iris_target))
    np.random.shuffle(iris_data)

    for index in range(150):
        if index <30:
            training_set[index] = iris_data[index]
        elif index < 90:
            validation_set[index-30] = iris_data[index]
        else:
            test_set[index - 90] = iris_data[index]

    root = make_tree(training_set, 0)

    validation(root)

    test(root)

def label_counts(rows):
    label_set = {}
    for row in rows:
        label = row[-1]
        if label not in label_set:
            label_set[label] = 1
        else:
            label_set[label] += 1
    return label_set

def gini(rows):
    counts = label_counts(rows)
    impurity = 1
    for label in counts:
        label_pi = counts[label] / float(len(rows))
        impurity -= label_pi ** 2
    return impurity

def info_gain(left, right, current_uncertainity):
    p = float(len(left))/ (len(left) + len(right))
    return current_uncertainity - p * gini(left) - (1 - p) * gini(right)

class Decision_Node:
    def __init__(self, rows, split_parameter, depth):
        self.split_parameter = split_parameter
        self.true_tree = None
        self.false_tree = None
        self.rows = rows
        self.leaf = False
        self.validRow = None
        self.testRow = None
        self.depth = depth

    def __init__(self, rows, true_tree, false_tree, split_parameter, depth):
        self.split_parameter = split_parameter
        self.true_tree = true_tree
        self.false_tree = false_tree
        self.rows = rows
        self.leaf = False
        self.validRow = None
        self.testRow = None
        self.depth = depth

class SplitParameter:
    def __init__(self, value, col):
        self.value = value
        self.col = col

class Leaf:
    def __init__(self, rows, depth):
        self.rows = rows
        self.validRow = None
        self.testRow = None
        self.depth = depth

def split(parameter, rows):
    true_rows, false_rows = [], []
    for row in rows:
        if row[parameter.col] <= parameter.value:
            true_rows.append(row)
        else:
            false_rows.append(row)
    return true_rows, false_rows

def find_split(rows):
    gain = 0
    selected_split_param = SplitParameter(-1, -1)
    parameter_size = len(training_set[0]) -1
    node_uncertainity = gini(rows)
    parameter_set = set()

    for col in range(parameter_size):
        for row in range(len(rows)):
            if not parameter_set.__contains__(rows[row][col]):
                parameter = SplitParameter(rows[row][col], col)
                parameter_set.add(parameter.value)
                true_rows, false_rows = split(parameter, rows)
                split_gain = info_gain(true_rows,false_rows, node_uncertainity)
                if split_gain > gain:
                    gain = split_gain
                    selected_split_param = parameter
    return gain, selected_split_param


def make_tree(rows, depth):
    gain, selected_parameter = find_split(rows)
    if gain == 0:
        return Leaf(rows, depth + 1)
    true_rows, false_rows = split(selected_parameter, rows)
    true_tree = make_tree(true_rows, depth +1)
    false_tree =  make_tree(false_rows, depth +1)
    return Decision_Node(rows, true_tree, false_tree, selected_parameter, depth)

#negatif ise durması lazm zaten


def BFS(root):
    print("BFS")
    queue = Queue()
    queue.put(root)
    while(not queue.empty()):
        dec = queue.get()
        if (isinstance(dec, Decision_Node)):
            print("Dec val")
            print(dec.split_parameter.value)
            print("Dec col")
            print(dec.split_parameter.col)
            print(label_counts(dec.rows))
            queue.put(dec.true_tree)
            queue.put(dec.false_tree)
        if (isinstance(dec, Leaf)):
            print("Leaf")
            print(label_counts(dec.rows))

def find_Error(train_labels, valid_labels):
    max_index = -1
    count = -1
    for i in range(3):
        if (i in train_labels and train_labels[i] > count):
            max_index = i
            count = train_labels[i]

    correct = 0
    sum = 0
    for i in range(3):
        if i in valid_labels:
            sum += valid_labels[i]
            if i == max_index:
                correct = valid_labels[i]
    return (sum - correct)/float(sum)

def validation(root):
    root.validRow = validation_set
    stack = LifoQueue()
    stack.put(root)
    while (not stack.empty()):
        dec = stack.get()
        if (isinstance(dec, Decision_Node)):
            true_rows, false_rows = split(dec.split_parameter, dec.validRow)
            dec.true_tree.validRow = true_rows
            dec.false_tree.validRow = false_rows

            prune = False
            if(not isinstance(dec.true_tree, Leaf) and find_Error(label_counts(dec.rows), label_counts(dec.validRow)) < find_Error(label_counts(dec.true_tree.rows), label_counts(dec.true_tree.validRow))) :
                dec.true_tree.leaf = True
                prune = True
            if(not isinstance(dec.false_tree, Leaf) and find_Error(label_counts(dec.rows), label_counts(dec.validRow)) < find_Error(label_counts(dec.false_tree.rows), label_counts(dec.false_tree.validRow))) :
                dec.false_tree.leaf = True
                prune = True
            if not prune:
                stack.put(dec.true_tree)
                stack.put(dec.false_tree)

            print("Validation error in depth ", dec.depth, count)
            print(find_Error(label_counts(dec.rows), label_counts(dec.validRow)))
        if (isinstance(dec, Leaf)):
            dec.valid = True
            print("Validation error leaf in depth ", dec.depth, count)
            print(find_Error(label_counts(dec.rows), label_counts(dec.validRow)))

gini_values = np.zeros(10).reshape(10, 1)
info_gains = np.zeros(10).reshape(10, 1)

def test(root):
    root.testRow = test_set
    stack = LifoQueue()
    stack.put(root)
    while (not stack.empty()):
        dec = stack.get()
        #gini_values[count] = np.append(gini_values[count], gini(dec.rows))
        if (isinstance(dec, Decision_Node)):
            if dec.leaf:
                print("Test error in depth ", dec.depth, count)
                print(find_Error(label_counts(dec.rows), label_counts(dec.testRow)))
            else:
                true_rows, false_rows = split(dec.split_parameter, dec.testRow)
                dec.true_tree.testRow = true_rows
                dec.false_tree.testRow = false_rows
                stack.put(dec.true_tree)
                stack.put(dec.false_tree)

        if (isinstance(dec, Leaf)):
            print("Test error in depth ", dec.depth, count)
            print(find_Error(label_counts(dec.rows), label_counts(dec.testRow)))
        #info_gains[count] = np.append(info_gains[count], info_gain(dec.true_tree, dec.false_tree, gini(dec)), axis= 1)

training_set = np.zeros(30*5).reshape(30,5) #30 60 60
validation_set = np.zeros(60*5).reshape(60,5)
test_set = np.zeros(60*5).reshape(60,5)


for count in range(10):
    main()