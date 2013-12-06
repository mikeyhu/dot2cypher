package net.invalidkeyword

import uk.co.turingatemyhamster.graphvizs.dsl._
import java.io.{InputStreamReader, FileInputStream}


case class CreateNode(nodeName:String) {
  val variableName = nodeName.toLowerCase().replaceAll(" ","")
  override def toString = s"CREATE (${variableName}:Class {name:'${nodeName}'})"
}

case object CreateNode {
  def apply(id:ID) : CreateNode = id match {
    case ID.Identifier(value) => CreateNode(value)
  }
}

case class CreateRelationship(nodeFrom:CreateNode, relationship:String, nodeTo:CreateNode) {
  override def toString = s"CREATE (${nodeFrom.variableName})-[:${relationship}]->(${nodeTo.variableName})"
}


object Dot2Cypher {


  def main(args:Array[String]) = convertToCypher(parseGraph())

  def parseGraph() : Graph = {
    val reader = new InputStreamReader(new FileInputStream("/Users/mikeyhu/tmp/code-graphs/core-2011.dot"))

    val parser = new DotAstParser
    import parser._
    val result : ParseResult[Graph] = parser.parseAll(graph,reader)

    result.get
  }

  def convertToCypher(element: Graph) = {
    //ignore NodeStatements for now. The relationships alone give us all the CreateNodes we need.
    val results: Seq[CreateRelationship] = element.statements.flatMap {
      case EdgeStatement(nodeFrom,nodesTo,_) => nodesTo.map { nodeTo => displayRelationship(nodeFrom.asInstanceOf[NodeId],nodeTo._2.asInstanceOf[NodeId])}
      case other => None
    }

    //output all the CreateNodes first, then the relationships
    val setOfCreates: Set[CreateNode] = results.flatMap(cr => Set(cr.nodeFrom,cr.nodeTo)).toSet
    setOfCreates.foreach(println(_))

    results.foreach(println(_))
  }

  def displayNode(node : NodeId) = {
    CreateNode(node.id)
  }

  def displayRelationship(nodeFrom:NodeId, nodeTo:NodeId) = {
    CreateRelationship(CreateNode(nodeFrom.id),"EXTENDS",CreateNode(nodeTo.id))
  }
}
